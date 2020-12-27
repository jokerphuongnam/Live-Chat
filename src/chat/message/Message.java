package chat.message;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToOne;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import chat.roomchat.Room;
import chat.user.User;
import chat.utils.MapperUtil;
import chat.utils.MapperUtil.DontMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "Messages")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Getter
@Setter
@NamedStoredProcedureQueries(value = {
		@NamedStoredProcedureQuery(name = "SP_GETMESSAGESBYTYPE_PAGING", procedureName = "SP_GETMESSAGESBYTYPE_PAGING", resultClasses = Message.class, parameters = {
				@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@PAGE", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@ROWSOFPAGE", mode = ParameterMode.IN, type = Integer.class),
				@StoredProcedureParameter(name = "@TYPE", mode = ParameterMode.IN, type = String.class) }),
		@NamedStoredProcedureQuery(name = "SP_GETLASTMESS", procedureName = "SP_GETLASTMESS", resultClasses = Message.class, parameters = {
				@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@SENDER", mode = ParameterMode.IN, type = Long.class) }),
		@NamedStoredProcedureQuery(name = "SP_SENDNGETLASTMESS", procedureName = "SP_SENDNGETLASTMESS", resultClasses = Message.class, parameters = {
				@StoredProcedureParameter(name = "@CONTENT_MESS", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@TYPE_MESS", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@ID_SENDER", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@ID_RECIVER", mode = ParameterMode.IN, type = Long.class) })})
public final class Message implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_message")
	@EqualsAndHashCode.Include
	private Long idMessage;
	@DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
	@Column(name = "send_time")
	private Date sendTime;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender")
	@DontMap
	private User sender;

	@ManyToOne
	@JoinColumn(name = "id_chat")
	private Room room;

	@OneToOne
	@JoinColumn(name = "id_message")
	private ContentMessage content;

	public Message mapByObject(Map<String, Object> mapFieldValue) {
		MapperUtil.mapForClass(this, this.getClass(), mapFieldValue);
		this.sender = new User().mapByObject(mapFieldValue);
		this.content = new ContentMessage().mapByObject(mapFieldValue);
		this.content.setMessage(this);
		return this;
	}
}
