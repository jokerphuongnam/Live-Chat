package chat.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import chat.member.Member;
import chat.message.ContentMessage;
import chat.message.Message;
import chat.roomchat.GroupChat;
import chat.roomchat.InboxChat;
import chat.roomchat.Room;
import chat.user.CurrentUser;
import chat.utils.ConstanceUtil;
import chat.utils.CryptUtil;
import chat.utils.JsonUtil;
import chat.utils.JsonUtil.ContentMessageForSendMessage;
import chat.utils.MapperUtil;
import chat.utils.SqlUtil;
import chat.utils.SqlUtil.SQLHandler;

@Controller
public final class SocketController {
	private final SimpMessagingTemplate template;
	ServletContext context;

	@Autowired
	public SocketController(SimpMessagingTemplate template, ServletContext context) {
		this.template = template;
		this.context = context;
	}

	ContentMessageForSendMessage jsonConfigs = new JsonUtil.ContentMessageForSendMessage();

	@RequestMapping(value = "/" + ConstanceUtil.MESSAGE + "/{idRoom}", method = RequestMethod.POST)
	private final @ResponseBody String send(@PathVariable("idRoom") String idRoomUrl,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson,
			@RequestBody String contentMessageJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		ContentMessage content = new JsonUtil<ContentMessage>(ContentMessage.class, jsonConfigs)
				.decode(contentMessageJson);
		AtomicReference<Long> idRoom = new AtomicReference<Long>(null);
		AtomicReference<Long> idReciver = new AtomicReference<Long>(null);
		try {
			idRoom.set(Long.valueOf(idRoomUrl));
		} catch (Exception e) {
			idReciver.set(MapperUtil.mapForField(CryptUtil.decrypt(MapperUtil.mapForField(idRoomUrl))));
		}
		AtomicReference<String> roomJson = new AtomicReference<String>("");
		sendMessage(currentUser, content, idRoom, currentUser.getIdUser(), idReciver.get(), (messsage) -> {
			messsage.setSender(currentUser);
			getMemberInRoom(currentUser, messsage, idRoom.get(), () -> {
				jsonConfigs.setEvent("sendMessage");
				jsonConfigs.setIdOldMessage(content.getIdContentMessage());
				roomJson.set(new JsonUtil<Room>(Room.class, jsonConfigs).endcode(messsage.getRoom()));
			}, (idUser) -> {
				template.convertAndSend("/topic/message/" + CryptUtil.encrypt(idUser), roomJson.get());
			});
		});
		return roomJson.get();
	}

	private final void sendMessage(CurrentUser currentUser, ContentMessage contentMessage, AtomicReference<Long> idRoom,
			Long idSender, Long idReciver, SendMessageForRoom event) {
		SqlUtil.executeStoreProduce("SP_SENDMESSCHAT", new SQLHandler() {

			@Override
			public void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@CONTENT_MESS", contentMessage.getContent(), String.class);
				setParameter("@TYPE_MESS", contentMessage.getType(), String.class);
				setParameter("@ID_CHAT", idRoom.get(), Long.class, true, ParameterMode.INOUT);
				setParameter("@ID_SENDER", idSender, Long.class);
				setParameter("@ID_RECIVER", idReciver, Long.class, true);
				query.execute();
				if (idRoom.get() == null) {
					idRoom.set(MapperUtil.mapForField(query.getOutputParameterValue("@ID_CHAT")));
				}
				this.orderExcute("SP_GETLASTMESS", oderQuery -> {
					setParameter("@ID_CHAT", idRoom.get(), Long.class);
					setParameter("@SENDER", idSender, Long.class);
					event.excute(new Message().mapByObject(
							new MapperUtil.MapColumn(SqlUtil.FIELD_MESSAGES, oderQuery.getSingleResult()).toMap()));
				});
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
	}

	private final void getMemberInRoom(CurrentUser currentUser, Message message, Long idRoom,
			ConvertMessageToJson eventConvert, SendMessageForUser sendMessageEvent) {
		SqlUtil.executeStoreProduce("SP_GETROOMBYIDROOM", new SQLHandler() {
			@Override
			public void success(StoredProcedureQuery query) {
				super.success(query);
				changeCurrentRoom(query);
				this.orderExcute("SP_GETMEMBERBYROOMID", (orderQuery) -> {
					addMember(orderQuery);
				});
				eventConvert.excute();
				message.getRoom().getMembers().forEach(member -> {
					sendMessageEvent.excute(member.getId().getIdUser());
				});
			}

			private final void changeCurrentRoom(StoredProcedureQuery query) {
				setParameter("@ID_ROOM", idRoom, Long.class);
				setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
				Map<String, Object> mapFieldValue = new MapperUtil.MapColumn(SqlUtil.FIELD_ROOM,
						query.getSingleResult()).toMap();
				if (mapFieldValue.get("founded_time") == null) {
					message.setRoom(new InboxChat().mapByObject(mapFieldValue));
				} else {
					message.setRoom(new GroupChat().mapByObject(mapFieldValue));
				}
				message.getRoom().sendMessage(message);
			}

			@SuppressWarnings("unchecked")
			private void addMember(StoredProcedureQuery query) {
				setParameter("@ID_CHAT", message.getRoom().getIdRoom(), Long.class);
				message.getRoom()
						.setMembers(AjaxController.getMembers(query.getResultList(), message.getRoom(), currentUser));
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
	}

	private interface SendMessageForRoom {
		void excute(Message message);
	}

	private interface ConvertMessageToJson {
		void excute();
	}

	private interface SendMessageForUser {
		void excute(Long idUser);
	}

	@RequestMapping(value = "/" + ConstanceUtil.CREATE_GROUP, method = RequestMethod.GET)
	private final @ResponseBody String createGroup(
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		AtomicReference<String> result = new AtomicReference<String>("");
		SqlUtil.executeStoreProduce("SP_CREATEGROUP", new SQLHandler() {

			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
				query.registerStoredProcedureParameter("@ID_CHAT", Long.class, ParameterMode.OUT);
				query.execute();
				result.set(
						resultJson(MapperUtil.mapForField(query.getOutputParameterValue("@ID_CHAT")), "createGroup"));
				template.convertAndSend("/topic/message/" + CryptUtil.encrypt(currentUser.getIdUser()), result.get());
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
		return result.get();
	}

	@RequestMapping(value = "/" + ConstanceUtil.LEAVE_GROUP + "/{idRoom}", method = RequestMethod.GET)
	private final @ResponseBody String leaveGroup(
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson, @PathVariable Long idRoom) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		AtomicReference<String> result = new AtomicReference<String>("");
		SqlUtil.executeStoreProduce("SP_LEAVEGROUP", new SQLHandler() {

			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
				setParameter("@ID_CHAT", idRoom, Long.class);
				query.execute();
				result.set(resultJson(idRoom, "leaveGroup"));
				template.convertAndSend("/topic/message/" + CryptUtil.encrypt(currentUser.getIdUser()), result.get());
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
		return result.get();
	}

	private final String resultJson(Long idRoom, String result) {
		return "{ \"id\": " + idRoom + " ,\"event\": \"" + result + "\" }";
	}

	@RequestMapping(value = "/" + ConstanceUtil.EDIT_NICKNAME + "/{idRoomUrl}", method = RequestMethod.POST)
	private final @ResponseBody String editNickname(
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson, @PathVariable String idRoomUrl,
			@RequestBody String nicknamesJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		AtomicReference<Long> idRoom = new AtomicReference<Long>(null);
		AtomicReference<Long> idReciver = new AtomicReference<Long>(null);
		try {
			idRoom.set(Long.valueOf(idRoomUrl));
		} catch (Exception e) {
			idReciver.set(MapperUtil.mapForField(CryptUtil.decrypt(MapperUtil.mapForField(idRoomUrl))));
		}
		String newNickname = null;
		try {
			newNickname = new JsonUtil<String>(String.class).decode(nicknamesJson).trim();
		} catch (Exception e) {
			newNickname = null;
		}
		final String newNicknameFormat = newNickname;
		SqlUtil.executeStoreProduce("SP_EDITNICKNAME", new SQLHandler() {
			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_CURRENT", currentUser.getIdUser(), Long.class);
				setParameter("@ID_CHAT", idRoom.get(), Long.class, ParameterMode.INOUT);
				setParameter("@ID_PARTNER", idReciver.get(), Long.class, true, ParameterMode.INOUT);
				setParameter("@NEW_NICKNAME", newNicknameFormat, String.class);
				query.registerStoredProcedureParameter("@FULLNAME", String.class, ParameterMode.OUT);
				query.execute();
				Long idPartner = MapperUtil.mapForField(query.getOutputParameterValue("@ID_PARTNER"));
				if (idPartner != null) {
					String results = results(MapperUtil.mapForField(query.getOutputParameterValue("@ID_CHAT")),
							newNicknameFormat == null
									? MapperUtil.mapForField(query.getOutputParameterValue("@FULLNAME"))
									: newNicknameFormat,
							"edit_nick_name");
					template.convertAndSend("/topic/message/" + CryptUtil.encrypt(idPartner), results);
				}
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
		return "{ \"event\": \"edit_nick_name\" }";
	}

	private String results(Long idChat, String name, String event) {
		return "{ \"id\": " + idChat + " ,\"event\": \"" + event + "\", \"name\": \"" + name + "\"}";
	}

	@RequestMapping(value = "/" + ConstanceUtil.EDIT_GROUP_NAME + "/{idRoomUrl}", method = RequestMethod.POST)
	private final @ResponseBody String editRoomName(
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson, @PathVariable String idRoomUrl,
			@RequestBody String groupnamesJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		AtomicReference<Long> idRoom = new AtomicReference<Long>(null);
		idRoom.set(Long.valueOf(idRoomUrl));
		String newGroupName = null;
		try {
			newGroupName = new JsonUtil<String>(String.class).decode(groupnamesJson).trim();
		} catch (Exception e) {
			newGroupName = null;
		}
		final String newGroupNameLambda = newGroupName;
		SqlUtil.executeStoreProduce("SP_CHANGEGROUPNAME", new SQLHandler() {
			List<Member> members = new ArrayList<Member>();

			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_CHAT", idRoom.get(), Long.class);
				setParameter("@NEWNAME", newGroupNameLambda, String.class);
				query.execute();
				String result = results(idRoom.get(), newGroupNameLambda, "edit_group_name");
				this.orderExcute("SP_GETMEMBERBYROOMID", (orderQuery) -> {
					addMember(orderQuery);
				});
				members.forEach(member -> {
					template.convertAndSend("/topic/message/" + CryptUtil.encrypt(member.getId().getIdUser()), result);
				});
			}

			@SuppressWarnings("unchecked")
			private void addMember(StoredProcedureQuery query) {
				setParameter("@ID_CHAT", idRoom.get(), Long.class);
				members = AjaxController.getMembers(query.getResultList(), null, currentUser);
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
		return "{ \"event\": \"edit_group_name\" }";
	}

	@RequestMapping(value = "/" + ConstanceUtil.ADD_GROUP + "/{idRoomUrl}", method = RequestMethod.POST)
	private final String addMemberToGroup(@RequestBody Long[] requestData) {
		SqlUtil.executeStoreProduce("SP_ADDMEMBER", new SQLHandler() {
			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_USER", requestData[0], Long.class);
				setParameter("@ID_CHAT", requestData[1], Long.class);
				query.execute();
				orderExcute("SP_GETROOMBY_IDROOM", orderExcute -> {
					setParameter("@ID_CHAT", requestData[1], Long.class);
					Map<String, Object> map = new MapperUtil.MapColumn(SqlUtil.FIELD_GETROOMCHAT_PAGING,
							orderExcute.getSingleResult()).toMap();
					map.put("event", "add_group");
					map.put("sender", CryptUtil.encrypt(map.get("sender")));
					template.convertAndSend("/topic/message/" + CryptUtil.encrypt(requestData[0]), map);
				});
			}

			@Override
			public void error(Exception e) {

			}
		});
		return "{ \"event\": \"add_member\" }";
	}

	private boolean saveImage(String filePathParam, MultipartFile avatar) {
		String uploadsDir = "/uploads/";
		String realPathtoUploads = context.getRealPath(uploadsDir);
		if (!new File(realPathtoUploads).exists()) {
			new File(realPathtoUploads).mkdir();
		}
		File dest = new File(realPathtoUploads + filePathParam);
		try {
			avatar.transferTo(dest);
			return true;
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void deleteImage(String filePathParam) {
		String uploadsDir = "/uploads/";
		String realPathtoUploads = context.getRealPath(uploadsDir);
		File dest = new File(realPathtoUploads + filePathParam);
		dest.delete();
	}

	@RequestMapping(value = "changeroomimage/{idroom}", method = RequestMethod.POST)
	private final String uploadImageGroup(@RequestParam("newImageRoom") MultipartFile avatar, @PathVariable Long idroom,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson, HttpServletResponse response,
			HttpServletRequest request) {
		AtomicReference<Boolean> isSuccess = new AtomicReference<Boolean>(true);
		if (avatar.isEmpty()) {
			return "redirect:/chat";
		} else {
			isSuccess.set(saveImage("group" + String.valueOf(idroom) + ".png", avatar));
		}
		if (isSuccess.get()) {
			SqlUtil.executeStoreProduce("SP_CHANGE_ROOMIMAGE", new SQLHandler() {
				@Override
				protected void success(StoredProcedureQuery query) {
					super.success(query);
					setParameter("@ID_ROOM", idroom, Long.class);
					setParameter("@NEW_IMAGE", "uploads/group" + String.valueOf(idroom) + ".png", String.class);
					query.execute();
					isSuccess.set(true);
				}

				@Override
				public void error(Exception e) {
					isSuccess.set(false);
					e.printStackTrace();
				}
			});
		}
		LoginController.addIdCurrentRoom(response, request, currentUserJson, idroom);
		return "redirect:/chat";
	}

	@RequestMapping(value = "changeroomimage/{idroom}", params = "removeImageGroup")
	private final String removeImgaeGroup(@PathVariable Long idroom) {
		deleteImage("group" + String.valueOf(idroom) + ".png");
		SqlUtil.executeStoreProduce("SP_CHANGE_ROOMIMAGE", new SQLHandler() {
			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_ROOM", idroom, Long.class);
				setParameter("@NEW_IMAGE", null, String.class, true);
				query.execute();
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
		return "redirect:/chat";
	}

	@RequestMapping(value = "uploadavatar", method = RequestMethod.GET)
	private final String uploadAvatar(ModelMap model,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		model.addAttribute("oldAvatar", SqlUtil.login(currentUser.getEmail(), currentUser.getPassword()).getAvatar());
		CurrentUser fullCurrentUser = SqlUtil.login(currentUser.getEmail(), currentUser.getPassword());
		String fullName = fullCurrentUser.getFirstName() + fullCurrentUser.getLastName() == null ? ""
				: " " + fullCurrentUser.getLastName();
		model.addAttribute("fullName", fullCurrentUser.getFirstName() + fullName);
		return EditController.rootPath + "uploadAvatar";
	}

	@RequestMapping(value = "uploadavatar", method = RequestMethod.POST)
	private final RedirectView uploadAvatar(RedirectAttributes redirectAttributes,
			@RequestParam("oldAvatar") MultipartFile avatar,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		AtomicReference<Boolean> isSuccess = new AtomicReference<Boolean>(true);
		if (avatar.isEmpty()) {
			return new RedirectView("chat");
		} else {
			isSuccess.set(saveImage("user" + String.valueOf(currentUser.getIdUser()) + ".png", avatar));
		}
		if (isSuccess.get()) {
			SqlUtil.executeStoreProduce("SP_UPLOAD_AVATAR", new SQLHandler() {
				@Override
				protected void success(StoredProcedureQuery query) {
					super.success(query);
					setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
					setParameter("@AVATAR", "uploads/user" + currentUser.getIdUser() + ".png", String.class);
					query.execute();
					isSuccess.set(true);
				}

				@Override
				public void error(Exception e) {
					redirectAttributes.addFlashAttribute("error", "Unkown error");
					isSuccess.set(false);
					e.printStackTrace();
				}
			});
		} else {
			redirectAttributes.addFlashAttribute("error", "Cannot upload image");
			return new RedirectView("uploadavatar");
		}
		if (isSuccess.get()) {
			return new RedirectView("chat");
		} else {
			return new RedirectView("uploadavatar");
		}
	}

	@RequestMapping(value = "uploadavatar", params = "removeBtn")
	private final RedirectView removeAvatar(HttpServletRequest request,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		AtomicReference<Boolean> isSuccess = new AtomicReference<Boolean>(true);
		deleteImage("group" + String.valueOf(currentUser.getIdUser()) + ".png");
		SqlUtil.executeStoreProduce("SP_UPLOAD_AVATAR", new SQLHandler() {
			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
				setParameter("@AVATAR", null, String.class, true);
				query.execute();
				isSuccess.set(true);
			}

			@Override
			public void error(Exception e) {
				isSuccess.set(false);
				e.printStackTrace();
			}
		});
		return new RedirectView("chat");
	}

	@RequestMapping(value = "uploadavatar", params = "cancelBtn")
	private final RedirectView cancelUploadAvatar(HttpServletRequest request) {
		return new RedirectView("chat");
	}
}