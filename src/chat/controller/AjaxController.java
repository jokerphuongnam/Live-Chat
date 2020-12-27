package chat.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import chat.member.Member;
import chat.member.MemberGroup;
import chat.member.MemberInbox;
import chat.roomchat.GroupChat;
import chat.roomchat.InboxChat;
import chat.roomchat.Room;
import chat.user.CurrentUser;
import chat.user.User;
import chat.utils.ConstanceUtil;
import chat.utils.CookieUtil;
import chat.utils.CryptUtil;
import chat.utils.JsonUtil;
import chat.utils.MapperUtil;
import chat.utils.SqlUtil;
import chat.utils.SqlUtil.SQLHandler;

@Controller
public final class AjaxController {

	@RequestMapping(("/" + ConstanceUtil.LOGOUT))
	private final void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Cookie cookie = CookieUtil.getCookies(request, ConstanceUtil.CURRENT_USER_COOKIE);
		cookie.setValue(null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		response.sendRedirect(request.getContextPath() + "/login");
	}

	public static final List<Member> getMembers(List<Object[]> results, Room currentRoom, CurrentUser currentUser) {
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
				member.setRoom(currentRoom);
				if (member.getId().getIdUser() == currentUser.getIdUser()) {
					User currentMember = member.getMember();
					currentUser.mapByUser(currentMember);
					members.add(0, member);
				} else {
					members.add(member);
				}
			}
		});
		return members;
	}

	@RequestMapping(value = "/" + ConstanceUtil.SEARCH + "/{searchWord}", method = RequestMethod.GET)
	private final @ResponseBody String searchUser(
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson, @PathVariable String searchWord) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		AtomicReference<String> usersJson = new AtomicReference<String>(null);
		SqlUtil.executeStoreProduce("SP_SEARCH", new SQLHandler() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@SEARCH", searchWord, String.class);
				setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
				List<Object[]> results = query.getResultList();
				List<User> users = new ArrayList<User>();
				for (Object result : results) {
					users.add(new User().mapByObject(new MapperUtil.MapColumn(SqlUtil.FIELD_USER, result).toMap()));
				}
				usersJson.set(new JsonUtil<List>(List.class, new JsonUtil.UsersJson()).endcode(users));
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}

		});
		return usersJson.get();
	}

	@RequestMapping(value = "/" + ConstanceUtil.CLICK_SEARCH + "/{idUserEncrypt}", method = RequestMethod.GET)
	private final @ResponseBody String chooseUserSearch(HttpServletResponse response, HttpServletRequest request,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson,
			@PathVariable String idUserEncrypt) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		Long idUser = MapperUtil.mapForField(CryptUtil.decrypt(idUserEncrypt.replace("|", "/")));
		AtomicReference<String> roomJson = new AtomicReference<String>("");
		SqlUtil.executeStoreProduce("SP_GETROOMBY_IDUSER", new SQLHandler() {
			private Room currentRoom;

			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				changeCurrentRoom(query);
				if (currentRoom != null) {
					roomJson.set(new JsonUtil<Room>(Room.class, new JsonUtil.RoomJson()).endcode(currentRoom));
				} else {
					roomJson.set("");
				}
			}

			private final void changeCurrentRoom(StoredProcedureQuery query) {
				setParameter("@ID_CURRENTUSER", currentUser.getIdUser(), Long.class);
				setParameter("@ID_RECIVEID", idUser, Long.class);
				if (query.getResultList().size() != 0) {
					currentRoom = new InboxChat()
							.mapByObject(new MapperUtil.MapColumn(SqlUtil.FIELD_ROOM, query.getSingleResult()).toMap());
				} else {
					currentRoom = null;
				}
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
		return roomJson.get();
	}

	@RequestMapping(value = "/" + ConstanceUtil.ADD_GROUP + "/{idRoomUrl}", method = RequestMethod.GET)
	private final @ResponseBody String getGroups(@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson,
			@PathVariable String idRoomUrl) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		AtomicReference<Long> idRoom = new AtomicReference<Long>(null);
		AtomicReference<Long> idReciver = new AtomicReference<Long>(null);
		AtomicReference<String> roomJson = new AtomicReference<String>("");
		try {
			idRoom.set(Long.valueOf(idRoomUrl));
			idReciver.set(null);
		} catch (Exception e) {
			idReciver.set(MapperUtil.mapForField(CryptUtil.decrypt(MapperUtil.mapForField(idRoomUrl))));
			idRoom.set(null);
		}
		SqlUtil.executeStoreProduce("SP_GETGROUPBY_IDUSER", new SQLHandler() {
			@SuppressWarnings("unchecked")
			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_CURRENTUSER", currentUser.getIdUser(), Long.class);
				setParameter("@ID_RECIVEID", idReciver.get(), Long.class, true, ParameterMode.INOUT);
				setParameter("@ID_GROUP", idRoom.get(), Long.class, true);
				List<GroupChat> groups = new ArrayList<GroupChat>();
				query.getResultList().forEach(result -> {
					groups.add(
							new GroupChat().mapByObject(new MapperUtil.MapColumn(SqlUtil.FIELD_ROOM, result).toMap()));
				});
				roomJson.set("{ \"rooms\": "
						+ new JsonUtil<GroupChat[]>(GroupChat[].class, new JsonUtil.RoomsJson())
								.endcode((GroupChat[]) groups.toArray(new GroupChat[groups.size()]))
						+ ", \"id\": " + query.getOutputParameterValue("@ID_RECIVEID") + "}");
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
		return roomJson.get();
	}
}
