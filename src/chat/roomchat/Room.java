package chat.roomchat;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ParameterMode;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import chat.member.Member;
import chat.message.Message;
import chat.utils.MapperUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuppressWarnings("serial")
@Entity
@Table(name = "Rooms")
//@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("CASE WHEN founded_time IS NOT NULL THEN 'GroupChat' ELSE 'InboxChat' END")
@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@NamedStoredProcedureQueries({
		@NamedStoredProcedureQuery(name = "SP_GETROOMCHAT_PAGING", procedureName = "SP_GETROOMCHAT_PAGING", resultClasses = {
				Room.class, GroupChat.class }, parameters = {
						@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class),
						@StoredProcedureParameter(name = "@PAGE", mode = ParameterMode.IN, type = Integer.class),
						@StoredProcedureParameter(name = "@ROW_OF_PAGE", mode = ParameterMode.IN, type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "SP_GETROOMBYIDROOM", procedureName = "SP_GETROOMBYIDROOM", resultClasses = {
				Room.class, GroupChat.class, InboxChat.class }, parameters = {
						@StoredProcedureParameter(name = "@ID_ROOM", mode = ParameterMode.IN, type = Long.class),
						@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class) }),
		@NamedStoredProcedureQuery(name = "SP_GETROOMBY_IDUSER", procedureName = "SP_GETROOMBY_IDUSER", resultClasses = {
				Room.class, GroupChat.class, InboxChat.class }, parameters = {
						@StoredProcedureParameter(name = "@ID_CURRENTUSER", mode = ParameterMode.IN, type = Long.class),
						@StoredProcedureParameter(name = "@ID_RECIVEID", mode = ParameterMode.IN, type = Long.class) }),
		@NamedStoredProcedureQuery(name = "SP_GETROOMBY_IDROOM", procedureName = "SP_GETROOMBY_IDROOM", resultClasses = {
				Room.class, GroupChat.class, InboxChat.class }, parameters = {
						@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class) }),
		@NamedStoredProcedureQuery(name = "SP_GETLAST_ROOM", procedureName = "SP_GETLAST_ROOM", resultClasses = {
				Room.class, GroupChat.class, InboxChat.class }, parameters = {
						@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class) })})
public abstract class Room implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_chat")
	@EqualsAndHashCode.Include
	protected Long idRoom;
	@Column(name = "is_waiting")
	protected Boolean isWaiting;

	@OneToOne(mappedBy = "room")
	@PrimaryKeyJoinColumn
	protected ColorMessage color;

	@OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	protected List<Message> messages;

	@OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	protected List<Member> members;

	@ConstructorProperties({ "idRoom", "isWaiting", "color", "message" })
	public Room(Long idRoom, Boolean isWaiting, ColorMessage color, Message message) {
		super();
		this.idRoom = idRoom;
		this.isWaiting = isWaiting;
		this.color = color;
		this.messages = new ArrayList<Message>();
		this.messages.add(message);
	}

	@ConstructorProperties({ "idRoom" })
	public Room(Long idRoom) {
		super();
		this.idRoom = idRoom;
	}

	protected void initMember() {
		if (this.members == null) {
			this.members = new ArrayList<Member>();
		}
	}

	public Member addMember(Member member) {
		initMember();
		this.members.add(member);
		return member;
	}

	public Member addCurrentUser(Member member) {
		initMember();
		this.members.add(0, member);
		return member;
	}

	public void initMessage() {
		if (this.messages == null) {
			this.messages = new ArrayList<Message>();
		}
	}

	public void setLastMessage(Message message) {
		this.initMessage();
		messages.add(message);
	}

	public Message sendMessage(Message message) {
		if (message.getIdMessage() != null) {
			this.initMessage();
			messages.add(message);
			return message;
		}
		return null;
	}

	public Member getMemberById(Long idUser) {
		if (this.members != null) {
			for (Member member : this.members) {
				if (member.getId().getIdUser() == idUser) {
					return member;
				}
			}
		}
		return null;
	}

	protected void initMessages() {
		if (messages == null)
			this.messages = new ArrayList<Message>();
	}

	public Message setMessage(Message message) {
		initMessages();
		messages.add(message);
		return message;
	}

	public Room mapByObject(Map<String, Object> mapFieldValue) {
		MapperUtil.mapForClass(this, this.getClass(), mapFieldValue);
		this.color = new ColorMessage().mapByObject(mapFieldValue);
		this.color.setRoom(this);
		Message newMessage = this.sendMessage(new Message().mapByObject(mapFieldValue));
		if (newMessage != null) {
			newMessage.setRoom(this);
		}
		return this;
	}

	@Override
	public boolean equals(Object object) {
		if (idRoom == ((Room) object).idRoom)
			return true;
		return false;
	}
}
