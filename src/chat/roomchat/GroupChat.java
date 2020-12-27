package chat.roomchat;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;

import org.hibernate.annotations.DiscriminatorFormula;
import org.springframework.format.annotation.DateTimeFormat;

import chat.member.Member;
import chat.member.MemberGroup;
import chat.message.Message;
import chat.utils.MapperUtil.NumberButString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
//@Table(name = "Rooms")
//@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorFormula("GroupChat")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@NamedStoredProcedureQueries({
	@NamedStoredProcedureQuery(name = "SP_GETGROUPBY_IDUSER", procedureName = "SP_GETGROUPBY_IDUSER", resultClasses = {
			Room.class, GroupChat.class }, parameters = {
					@StoredProcedureParameter(name = "@ID_CURRENTUSER", mode = ParameterMode.IN, type = Long.class),
					@StoredProcedureParameter(name = "@ID_RECIVEID", mode = ParameterMode.INOUT, type = Long.class),
					@StoredProcedureParameter(name = "@ID_GROUP", mode = ParameterMode.IN, type = Long.class)})})
public final class GroupChat extends Room {
	@Column(name = "image_group", columnDefinition = "NVARCHAR default ''")
	private String imageGroup;
	@DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
	@Column(name = "founded_time")
	private Date foundedTime;
	@Column(name = "name_group", length = 200, columnDefinition = "NVARCHAR default ''")
	@NumberButString
	private String nameGroup;

	@Builder(builderMethodName = "groupChatBuilder")
	public GroupChat(Long idRoom, String nameGroup, String imageGroup, Boolean isWaiting, ColorMessage color,
			ColorMessage colorDate, Date foudedTime, Message message) {
		super(idRoom, isWaiting, color, message);
		this.nameGroup = nameGroup;
		this.imageGroup = imageGroup;
		this.foundedTime = foudedTime;
	}

	public Member addMember(MemberGroup member) {
		return super.addMember(member);
	}

	public Member addCurrentUser(Member member) {
		return super.addCurrentUser(member);
	}

	@Override
	public GroupChat mapByObject(Map<String, Object> mapFieldValue) {
		super.mapByObject(mapFieldValue);
		return this;
	}
}