package chat.utils;

import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.procedure.internal.ProcedureParameterImpl;

import chat.member.Member;
import chat.member.MemberGroup;
import chat.member.MemberInbox;
import chat.message.ContentMessage;
import chat.message.Message;
import chat.roomchat.ColorMessage;
import chat.roomchat.GroupChat;
import chat.roomchat.InboxChat;
import chat.roomchat.Room;
import chat.user.CurrentUser;
import chat.user.Setting;
import chat.user.User;
import lombok.Setter;

public final class SqlUtil {
	private static String localPort = "53723";

	@Transactional
	public static final void executeStoreProduce(String nameStoreProduce,
			SQLHandler handler, Class<?>... clazz) {
		try (Session session = createConfiguration("sa", "123").buildSessionFactory().openSession()) {
			handler.setSession(session);
			handler.success(session.createStoredProcedureQuery(nameStoreProduce, clazz));
		} catch (Exception e) {
			handler.error(e);
		}
	}

	public static final CurrentUser login(String username, String password) {
		try (SessionFactory sessionFactory = createConfiguration("sa", "123").buildSessionFactory();
				Session session = sessionFactory.openSession();) {
			StoredProcedureQuery query = session.createStoredProcedureQuery("SP_LOGIN");
			query.registerStoredProcedureParameter("@USERNAME", String.class, ParameterMode.IN);
			if(!(username.contains("@") || username.chars().allMatch(Character::isDigit))) {
				username += "@gmail.com";
			}
			query.setParameter("@USERNAME", username);
			query.registerStoredProcedureParameter("@PASSWORD", String.class, ParameterMode.IN);
			query.setParameter("@PASSWORD", password);
			query.setMaxResults(1);
			return new CurrentUser()
					.mapByObject(new MapperUtil.MapColumn(SqlUtil.FIELD_LOGIN, query.getSingleResult()).toMap());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final Configuration createConfiguration(String username, String password) {
		Configuration configuration = new Configuration();

		configuration.setProperty(Environment.URL, "jdbc:sqlserver://localhost:" + localPort + "; Database= ChatApp");
		configuration.setProperty(Environment.USER, username);
		configuration.setProperty(Environment.PASS, password);
		configuration.setProperty(Environment.DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		configuration.setProperty(Environment.DIALECT, "org.hibernate.dialect.SQLServerDialect");
		configuration.setProperty("hibernate.connection.CharSet", "utf8");
		configuration.setProperty("hibernate.connection.characterEncoding", "utf8");
		configuration.setProperty("hibernate.connection.useUnicode", "true");
		configuration.setProperty("hibernate.hbm2ddl.auto", "validate");

		configuration.addAnnotatedClass(CurrentUser.class);
		configuration.addAnnotatedClass(User.class);
		configuration.addAnnotatedClass(Setting.class);

		configuration.addAnnotatedClass(Room.class);
		configuration.addAnnotatedClass(GroupChat.class);
		configuration.addAnnotatedClass(InboxChat.class);

		configuration.addAnnotatedClass(ColorMessage.class);

		configuration.addAnnotatedClass(Member.class);
		configuration.addAnnotatedClass(MemberGroup.class);
		configuration.addAnnotatedClass(MemberInbox.class);

		configuration.addAnnotatedClass(Message.class);

		configuration.addAnnotatedClass(ContentMessage.class);

		return configuration;
	}

	public static final String[] FIELD_GETROOMCHAT_PAGING = { "id_chat", "is_waiting", "name_group", "image_group",
			"founded_time", "id_color_message", "start_color", "end_color", "name_color", "id_content_message",
			"send_time", "sender", "id_message", "type_message", "content_message" };
	public static final String[] FIELD_MESSAGES = { "id_chat", "id_message", "id_user", "send_time",
			"id_content_message", "content_message", "type_message" };
	public static final String[] FIELD_MEMBERS = { "id_chat", "id_user", "avatar", "first_name", "last_name",
			"nick_name", "date_of_join", "is_admin" };
	private static final String[] FIELD_LOGIN = { "id_user", "avatar", "phone_number", "email", "password",
			"first_name", "last_name", "sex", "birth_day", "address", "status", "join_time", "id_setting", "theme",
			"is_show_email", "is_show_phone", "devices_logged" };
	public static final String[] FIELD_ROOM = { "id_chat", "is_waiting", "image_group", "founded_time", "name_group",
			"id_color_message", "start_color", "end_color", "name_color" };
	public static final String[] FIELD_USER = { "id_user", "avatar", "first_name", "last_name",
			"sex", "birth_day", "address", "status", "join_time" };

	public static abstract class SQLHandler {
		private StoredProcedureQuery query;
		@Setter
		protected Session session;

		protected void success(StoredProcedureQuery query) {
			this.query = query;
		}

		public abstract void error(Exception e);

		protected void setParameter(String nameParameter, Object parameter, Class<?> clazz, ParameterMode... mode) {
			setParameter(nameParameter, parameter, clazz, false, mode);
		}

		protected void setParameter(String nameParameter, Object parameter, Class<?> clazz, boolean canNull,
				ParameterMode... mode) {
			query.registerStoredProcedureParameter(nameParameter, clazz, mode.length < 1 ? ParameterMode.IN : mode[0]);
			if (canNull) {
				setNullParameter(nameParameter);
			}
			query.setParameter(nameParameter, parameter);
		}

		@SuppressWarnings("rawtypes")
		private void setNullParameter(String nameParameter) {
			for (Parameter parameter : query.getParameters()) {
				if (parameter.getName() == nameParameter) {
					((ProcedureParameterImpl) parameter).enablePassingNulls(true);
				}
			}
		}

		protected void orderExcute(String nameStoreProduce, OrderExecute execute, Class<?>... clazz) {
			query = session.createStoredProcedureQuery(nameStoreProduce, clazz);
			execute.execute(query);
		}

		public interface OrderExecute {
			void execute(StoredProcedureQuery query);
		}
	}
}
