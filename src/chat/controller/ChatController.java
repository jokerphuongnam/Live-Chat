package chat.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import chat.member.Member;
import chat.member.MemberGroup;
import chat.member.MemberInbox;
import chat.message.Message;
import chat.roomchat.GroupChat;
import chat.roomchat.InboxChat;
import chat.roomchat.Room;
import chat.user.CurrentUser;
import chat.user.User;
import chat.utils.JsonUtil;
import chat.utils.MapperUtil;
import chat.utils.SqlUtil;
import chat.utils.SqlUtil.SQLHandler;
import chat.utils.ConstanceUtil;

@Controller
public final class ChatController {

	@RequestMapping(value = "chat")
	private final String index(@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson, ModelMap model,
			HttpServletRequest request) {
		CurrentUser currentUserDto = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		Long idCurrentRoom = LoginController.getRoomFromAccountLogged(request);
		if (idCurrentRoom != -1) {
			return "redirect:/chat/" + idCurrentRoom;
		}
		AtomicReference<Long> idRoom = new AtomicReference<Long>(-1L);
		SqlUtil.executeStoreProduce("SP_GETLAST_ROOM", new SQLHandler() {
			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_USER", currentUserDto.getIdUser(), Long.class);
				@SuppressWarnings("rawtypes")
				List results = query.getResultList();
				if (results.size() == 0) {
					idRoom.set(-1L);
				} else {
					idRoom.set(MapperUtil.mapForField(results.get(0)));
				}
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
		if (idRoom.get() == -1) {
			CurrentUser currentUser = SqlUtil.login(currentUserDto.getEmail(), currentUserDto.getPassword());
			model.addAttribute("currentUser", currentUser);
			return "chat/Chat";
		} else {
			return "redirect:/chat/" + idRoom.get();
		}
	}

	@RequestMapping(value = "chat/{idRoom}")
	private final String messagesOfRoom(@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson,
			HttpServletResponse response, HttpServletRequest request, ModelMap model, @PathVariable Long idRoom) {
		CurrentUser currentUserDto = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		CurrentUser currentUser = SqlUtil.login(currentUserDto.getEmail(), currentUserDto.getPassword());
		model.addAttribute("currentUser", currentUser);
		AtomicReference<Long> roomId = new AtomicReference<Long>(-1L);
		SqlUtil.executeStoreProduce("SP_GETROOMCHAT_PAGING", new SqlUtil.SQLHandler() {
			private List<Room> rooms = new ArrayList<Room>();
			private Room currentRoom = null;

			@Override
			public final void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
				setParameter("@PAGE", 1, Integer.class);
				setParameter("@ROW_OF_PAGE", 10, Integer.class);
				query.execute();
				@SuppressWarnings("unchecked")
				List<Object[]> results = query.getResultList();
				results.forEach(result -> {
					Map<String, Object> map = new MapperUtil.MapColumn(SqlUtil.FIELD_GETROOMCHAT_PAGING, result)
							.toMap();
					if (map.get("founded_time") == null) {
						rooms.add(new InboxChat().mapByObject(map));
					} else {
						rooms.add(new GroupChat().mapByObject(map));
					}
				});
				if (!rooms.isEmpty()) {
					rooms.forEach(room -> {
						orderExcute("SP_GETMEMBERBYROOMID", orderQuery -> {
							getMembersRoom(orderQuery, room);
						});
					});
					model.addAttribute("rooms", rooms);
					for (Room room : rooms) {
						if (room.getIdRoom() == idRoom) {
							currentRoom = room;
						}
					}
					if (currentRoom == null) {
						orderExcute("SP_GETROOMBYIDROOM", orderQuery -> {
							changeCurrentRoom(orderQuery, idRoom);
						}, Room.class, GroupChat.class, InboxChat.class);
					}
					if (currentRoom != null) {
						orderExcute("SP_GETMESSAGESBYTYPE_PAGING", orderQuery -> {
							addMessages(orderQuery);
						});
						model.addAttribute("currentRoom", currentRoom);
						LoginController.addIdCurrentRoom(response, request, currentUserJson, idRoom);
					} else {
						LoginController.addIdCurrentRoom(response, request, currentUserJson, rooms.get(0).getIdRoom());
						roomId.set(rooms.get(0).getIdRoom());
					}
				} else {
					model.addAttribute("currentRoom", null);
					model.addAttribute("rooms", null);
				}
			}

			@Override
			public final void error(Exception e) {
				e.printStackTrace();
			}

			private final void changeCurrentRoom(StoredProcedureQuery query, Long idCurrentRoom) {
				setParameter("@ID_ROOM", idCurrentRoom, Long.class);
				setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
				if (query.getResultList().size() > 0) {
					Map<String, Object> mapFieldValue = new MapperUtil.MapColumn(SqlUtil.FIELD_ROOM,
							query.getSingleResult()).toMap();
					if (mapFieldValue.get("founded_time") == null) {
						currentRoom = new InboxChat().mapByObject(mapFieldValue);
					} else {
						currentRoom = new GroupChat().mapByObject(mapFieldValue);
					}
				} else {
					currentRoom = null;
				}
			}

			private final void getMembersRoom(StoredProcedureQuery orderQuery, Room room) {
				setParameter("@ID_CHAT", room.getIdRoom(), Long.class);
				@SuppressWarnings("unchecked")
				List<Object[]> results = orderQuery.getResultList();
				List<Member> members = new ArrayList<Member>();
				results.forEach(result -> {
					Map<String, Object> map = new MapperUtil.MapColumn(SqlUtil.FIELD_MEMBERS, result).toMap();
					Member member = null;
					if (map.get("date_of_join") != null) {
						member = new MemberGroup().mapByObject(map);
					} else {
						member = new MemberInbox().mapByObject(map);
					}
					if (member != null) {
						member.setRoom(room);
						if (member.getId().getIdUser() == currentUser.getIdUser()) {
							User currentMember = member.getMember();
							currentUser.mapByUser(currentMember);
							members.add(0, member);
						} else {
							members.add(member);
						}
					}
				});
				room.setMembers(members);
			}

			private final void addMessages(StoredProcedureQuery orderQuery) {
				setParameter("@ID_CHAT", idRoom, Long.class);
				setParameter("@PAGE", Long.valueOf(1), Long.class);
				setParameter("@ROWSOFPAGE", Integer.valueOf(30), Integer.class);
				setParameter("@TYPE", "", String.class);
				@SuppressWarnings("unchecked")
				List<Object[]> results = orderQuery.getResultList();
				List<Message> messages = new ArrayList<Message>();
				results.forEach(result -> {
					Message message = new Message()
							.mapByObject(new MapperUtil.MapColumn(SqlUtil.FIELD_MESSAGES, result).toMap());
					message.setSender(currentRoom.getMemberById(message.getSender().getIdUser()).getMember());
					messages.add(message);
				});
				currentRoom.setMessages(messages);
			}

		});
		if (roomId.get() != -1) {
			return "redirect:/chat/" + roomId.get();
		}
		return "chat/Chat";
	}

	@RequestMapping(value = "viewavatar")
	private final String viewAvatar(ModelMap model,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		CurrentUser fullCurrentUser = SqlUtil.login(currentUser.getEmail(), currentUser.getPassword());
		model.addAttribute("avatar", fullCurrentUser.getAvatar());
		String fullName = fullCurrentUser.getFirstName() + fullCurrentUser.getLastName() == null ? ""
				: " " + fullCurrentUser.getLastName();
		model.addAttribute("fullName", fullCurrentUser.getFirstName() + fullName);
		return "chat/viewAvatar";
	}

	@RequestMapping(value = "viewavatar", method = RequestMethod.POST)
	private final RedirectView changeAvatar() {
		return new RedirectView("uploadavatar");
	}

	@RequestMapping(value = "viewAvatar")
	private final RedirectView cancelViewAvatar() {
		return new RedirectView("chat");
	}
}
