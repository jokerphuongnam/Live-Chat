package chat.member;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

import chat.roomchat.Room;
import chat.user.User;
import chat.utils.MapperUtil;
import chat.utils.MapperUtil.NumberButString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuppressWarnings("serial")
@Entity
@Table(name = "Members")
//@MappedSuperclass
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@DiscriminatorFormula("CASE WHEN date_of_join IS NOT NULL THEN 'MemberGroup' ELSE 'MemberInbox' END")
//@DiscriminatorColumn(name = "date_of_join", columnDefinition = "smalldatetime")
@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@NamedStoredProcedureQueries(value = {
		@NamedStoredProcedureQuery(name = "SP_GETMEMBERBYUSERID", procedureName = "SP_GETMEMBERBYUSERID", resultClasses = {
				Member.class, MemberGroup.class }, parameters = {
						@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class),
						@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class) }),
		@NamedStoredProcedureQuery(name = "SP_GETMEMBERBYROOMID", procedureName = "SP_GETMEMBERBYROOMID", resultClasses = {
				Member.class, MemberGroup.class }, parameters = {
						@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class) }),
		@NamedStoredProcedureQuery(name = "SP_GETMBERBYROOMIDWHENINBOX", procedureName = "SP_GETMBERBYROOMIDWHENINBOX", resultClasses = Member.class, parameters = {
				@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class) }) })
public abstract class Member implements Serializable {
	@EmbeddedId
	@EqualsAndHashCode.Include
	protected MemberPrimaryKey id;

	@ManyToOne
	@MapsId("roomId")
	@JoinColumn(name = "id_chat")
	protected Room room;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "id_user")
	protected User member;

	@Column(name = "nick_name", length = 200, columnDefinition = "NVARCHAR")
	@NumberButString
	protected String nickName;

	public Member mapByObject(Map<String, Object> mapFieldValue) {
		MapperUtil.mapForClass(this, this.getClass(), mapFieldValue);
		this.id = new MemberPrimaryKey().mapByObject(mapFieldValue);
		this.member = new User().mapByObject(mapFieldValue);
		return this;
	}
}