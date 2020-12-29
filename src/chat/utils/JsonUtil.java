package chat.utils;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import chat.message.ContentMessage;
import chat.message.Message;
import chat.roomchat.GroupChat;
import chat.roomchat.InboxChat;
import chat.roomchat.Room;
import chat.user.CurrentUser;
import chat.user.User;
import lombok.Getter;
import lombok.Setter;

public final class JsonUtil<T> {
	private Gson gson;

	private final Class<T> clazz;

	private void initGson() {
		if (gson == null) {
			gson = new GsonBuilder().disableHtmlEscaping().create();
		}
	}

	public JsonUtil(Class<T> clazz, Object serializer) {
		super();
		this.clazz = clazz;
		this.gson = new GsonBuilder().disableHtmlEscaping().registerTypeHierarchyAdapter(clazz, serializer).create();
	}

	public JsonUtil(Class<T> clazz) {
		super();
		this.clazz = clazz;
		this.gson = null;
	}

	public final T decode(String json) {
		initGson();
		return gson.fromJson(json, clazz);
	}

	public final String endcode(T object) {
		initGson();
		if (object == null)
			return null;
		return gson.toJson(object);
	}

	public static final class AccountsLoggedConfig
			implements JsonSerializer<HashMap<String, Long>>, JsonDeserializer<HashMap<String, Long>> {
		@Override
		public final JsonElement serialize(HashMap<String, Long> src, Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			src.forEach((key, value) -> {
				jsonObject.addProperty(key, Long.valueOf(value));
			});
			return jsonObject;
		}

		@Override
		public final HashMap<String, Long> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			HashMap<String, Long> map = new HashMap<String, Long>();
			JsonObject jsonObject = json.getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();// will return members of your object
			for (Map.Entry<String, JsonElement> entry : entries) {
				map.put(entry.getKey(), entry.getValue().getAsLong());
			}
			return map;
		}
	}

	public static final class CurrentUserForCookiesConfigs
			implements JsonSerializer<CurrentUser>, JsonDeserializer<CurrentUser> {
		@Override
		public final JsonElement serialize(CurrentUser src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(FIELDS[0], CryptUtil.encrypt(src.getIdUser()));
			jsonObject.addProperty(FIELDS[1], CryptUtil.encrypt(src.getPassword().trim()));
			jsonObject.addProperty(FIELDS[2], CryptUtil.encrypt(src.getEmail().trim().toLowerCase()));
			return jsonObject;
		}

		@Override
		public final CurrentUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return new CurrentUser().mapByObject(createMapDeserialize(json.getAsJsonObject()));
		}

		private static final Map<String, Object> createMapDeserialize(JsonObject jsonObject) {
			Map<String, Object> mapFieldValue = new HashMap<String, Object>();
			mapFieldValue.put("idUser",
					MapperUtil.mapForField(CryptUtil.decrypt(MapperUtil.mapForField(jsonObject.get(FIELDS[0])))));
			mapFieldValue.put("email",
					MapperUtil.mapForField(CryptUtil.decrypt(MapperUtil.mapForField(jsonObject.get(FIELDS[2])))));
			mapFieldValue.put("password",
					MapperUtil.mapForField(CryptUtil.decrypt(MapperUtil.mapForField(jsonObject.get(FIELDS[1])))));
			return mapFieldValue;
		}

		public static final String[] FIELDS = { "id", "fhasbd", "hasdb" };
	}

	public static final class ContentMessageForSendMessage
			implements JsonSerializer<Room>, JsonDeserializer<ContentMessage> {

		@Override
		public final JsonElement serialize(Room src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(ROOM_FIELDS[0], src.getIdRoom());
			GroupChat groupChat = src instanceof GroupChat ? (GroupChat) src : null;
			User partnerUser = groupChat == null ? src.getMembers().get(1).getMember() : null;
			String nickName = null;
			if (partnerUser != null) {
				nickName = src.getMemberById(partnerUser.getIdUser()).getNickName();
			}
			jsonObject.addProperty(ROOM_FIELDS[1], src instanceof GroupChat);
			jsonObject.addProperty(ROOM_FIELDS[2],
					src instanceof GroupChat ? groupChat.getNameGroup()
							: nickName != null ? nickName
									: String.join(" ", partnerUser.getFirstName(), partnerUser.getLastName()));
			jsonObject.addProperty(ROOM_FIELDS[3],
					src instanceof GroupChat ? groupChat.getImageGroup() : partnerUser.getAvatar());
			if (partnerUser != null) {
				jsonObject.addProperty(ROOM_FIELDS[4],
						partnerUser.getStatus() != null
								? new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(partnerUser.getStatus())
								: "online");
			}
			JsonArray messages = new JsonArray();
			src.getMessages().forEach(mess -> {
				mess.setRoom(src);
				messages.add(messToJsonElement(mess, typeOfSrc, context, MESSAGE_FIELDS));
			});
			jsonObject.add(ROOM_FIELDS[5], messages);
			jsonObject.addProperty(ROOM_FIELDS[6], event);
			jsonObject.addProperty("id_old_message", idOldMessage);
			return jsonObject;
		}

		@Getter
		@Setter
		private String event = null;
		@Getter
		@Setter
		private Long idOldMessage = null;

		@Override
		public final ContentMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return new ContentMessage().mapByObject(createMapDeserialize(json.getAsJsonObject()));
		}

		private static final Map<String, Object> createMapDeserialize(JsonObject jsonObject) {
			Map<String, Object> mapFieldValue = new HashMap<String, Object>();
			mapFieldValue.put(MESSAGE_FIELDS[1], MapperUtil.mapForField(jsonObject.get(MESSAGE_FIELDS[1])));
			mapFieldValue.put(MESSAGE_FIELDS[2], MapperUtil.mapForField(jsonObject.get(MESSAGE_FIELDS[2])));
			mapFieldValue.put("id_content_message", MapperUtil.mapForField(jsonObject.get("id_before_send")));
			return mapFieldValue;
		}

		public static final String[] ROOM_FIELDS = { "id", "isGroup", "name", "image", "offline", "messages", "event" };
		public static final String[] MESSAGE_FIELDS = { "id", "content", "type", "name", "avatar", "nick_name",
				"send_time", "id_room", "name_group", "id_sender" };
	}

	private static JsonElement messToJsonElement(Message src, Type typeOfSrc, JsonSerializationContext context,
			String[] FIELDS) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FIELDS[0], src.getIdMessage());
		jsonObject.addProperty(FIELDS[1], src.getContent().getContent());
		jsonObject.addProperty(FIELDS[2], src.getContent().getType());
		jsonObject.addProperty(FIELDS[3],
				String.join(" ", src.getSender().getFirstName(), src.getSender().getLastName()));
		jsonObject.addProperty(FIELDS[4], src.getSender().getAvatar());
		String nickName = src.getRoom().getMemberById(src.getSender().getIdUser()).getNickName();
		jsonObject.addProperty(FIELDS[5], (nickName == null) ? null : nickName);
		jsonObject.addProperty(FIELDS[6], new SimpleDateFormat("hh:mm dd/MM/yyyy").format(src.getSendTime()));
		jsonObject.addProperty(FIELDS[7], src.getRoom().getIdRoom());
		jsonObject.addProperty(FIELDS[8],
				src.getRoom() instanceof InboxChat ? null : ((GroupChat) src.getRoom()).getNameGroup());
		jsonObject.addProperty(FIELDS[9], CryptUtil.encrypt(src.getSender().getIdUser()));
		return jsonObject;
	}

	public static final class RoomJson implements JsonSerializer<Room>, JsonDeserializer<Room> {

		@Getter
		@Setter
		private String event = null;

		@Override
		public JsonElement serialize(Room src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(ROOM_FIELDS[0], src.getIdRoom());
			GroupChat groupChat = src instanceof GroupChat ? (GroupChat) src : null;
			if (src.getMembers() != null) {
				User partnerUser = groupChat == null ? src.getMembers().get(1).getMember() : null;
				String nickName = null;
				if (partnerUser != null) {
					nickName = src.getMemberById(partnerUser.getIdUser()).getNickName();
				}
				jsonObject.addProperty(ROOM_FIELDS[1], src instanceof GroupChat);
				jsonObject.addProperty(ROOM_FIELDS[2],
						src instanceof GroupChat ? groupChat.getNameGroup()
								: nickName != null ? nickName
										: String.join(" ", partnerUser.getFirstName(), partnerUser.getLastName()));
				jsonObject.addProperty(ROOM_FIELDS[3],
						src instanceof GroupChat ? groupChat.getImageGroup() : partnerUser.getAvatar());
				if (partnerUser != null) {
					jsonObject.addProperty(ROOM_FIELDS[4],
							partnerUser.getStatus() != null
									? new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(partnerUser.getStatus())
									: "online");
				}
			}
			if (src.getMessages() != null) {
				JsonArray messages = new JsonArray();
				src.getMessages().forEach(mess -> {
					mess.setRoom(src);
					messages.add(messToJsonElement(mess, typeOfSrc, context, MESSAGE_FIELDS));
				});
				jsonObject.add(ROOM_FIELDS[5], messages);
			}
			jsonObject.addProperty(ROOM_FIELDS[6], event);
			return jsonObject;
		}

		@Override
		public Room deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return null;
		}

		public static final String[] ROOM_FIELDS = { "id", "isGroup", "name", "image", "offline", "messages", "event" };
		public static final String[] MESSAGE_FIELDS = { "id", "content", "type", "name", "avatar", "nick_name",
				"send_time", "id_room", "name_group", "id_sender" };
	}

	public static final class UserJson implements JsonSerializer<User>, JsonDeserializer<User> {

		@Override
		public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
			return createUserJson(src, typeOfSrc, context, FIELDS);
		}

		@Override
		public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return null;
		}

		public static final String[] FIELDS = { "id_user", "avatar", "first_name", "last_name", "sex", "birth_day",
				"address", "status", "join_time" };
	}

	private static final JsonElement createUserJson(User src, Type typeOfSrc, JsonSerializationContext context,
			String[] FIELDS) {
		JsonObject jsonObject = new JsonObject();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
		jsonObject.addProperty(FIELDS[0], CryptUtil.encrypt(src.getIdUser()));
		jsonObject.addProperty(FIELDS[1], src.getAvatar());
		jsonObject.addProperty(FIELDS[2], src.getFirstName().trim());
		jsonObject.addProperty(FIELDS[3], src.getLastName().trim());
		jsonObject.addProperty(FIELDS[4], src.getSex());
		if (src.getBirthDay() != null) {
			jsonObject.addProperty(FIELDS[5], simpleDateFormat.format(src.getBirthDay()));
		}
		jsonObject.addProperty(FIELDS[6], src.getAddress());
		if (src.getStatus() != null) {
			jsonObject.addProperty(FIELDS[7], new SimpleDateFormat("hh:mm dd/MM/yyyy").format(src.getStatus()));
		}
		jsonObject.addProperty(FIELDS[8], simpleDateFormat.format(src.getJoinTime()));
		return jsonObject;
	}

	public static final class UsersJson implements JsonSerializer<List<User>>, JsonDeserializer<List<User>> {
		@Override
		public JsonElement serialize(List<User> src, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray jsonArray = new JsonArray();
			for (User user : src) {
				jsonArray.add(createUserJson(user, typeOfSrc, context, FIELDS));
			}
			return jsonArray;
		}

		@Override
		public List<User> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return null;
		}

		public static final String[] FIELDS = { "id_user", "avatar", "first_name", "last_name", "sex", "birth_day",
				"address", "status", "join_time" };
	}

	private static final JsonElement createRoomJson(GroupChat src, Type typeOfSrc, JsonSerializationContext context,
			String[] FIELDS) {
		JsonObject json = new JsonObject();
		json.addProperty(FIELDS[0], src.getIdRoom());
		json.addProperty(FIELDS[1], src.getImageGroup());
		json.addProperty(FIELDS[2], src.getNameGroup());
		return json;
	}

	public static class RoomsJson implements JsonSerializer<GroupChat[]>, JsonDeserializer<GroupChat[]> {
		@Override
		public JsonElement serialize(GroupChat[] src, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray jsonArray = new JsonArray();
			for (GroupChat room : src) {
				jsonArray.add(createRoomJson(room, typeOfSrc, context, FIELDS));
			}
			return jsonArray;
		}

		@Override
		public GroupChat[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return null;
		}

		private String[] FIELDS = { "id", "image", "name" };
	}
}
