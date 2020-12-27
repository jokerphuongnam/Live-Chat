package chat.member;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;

import org.springframework.format.annotation.DateTimeFormat;

import chat.roomchat.Room;
import chat.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
//@Table(name = "Members")
//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorFormula("not null")
//@DiscriminatorColumn(name = "date_of_join", columnDefinition = "smalldatetime")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public final class MemberGroup extends Member {
	@Column(name = "date_of_join", insertable = false, updatable = false)
	@DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
	private Date dateOfJoin;
	@Column(name = "is_admin")
	private Boolean isAdmin;

	@Builder(builderMethodName = "groupMemberBuilder")
	public MemberGroup(MemberPrimaryKey id, Room room, User member, String nickName, Date dateOfJoin, Boolean isAdmin) {
		super(id, room, member, nickName);
		this.dateOfJoin = dateOfJoin;
		this.isAdmin = isAdmin;
	}

	@Override
	public Member mapByObject(Map<String, Object> mapFieldValue) {
		super.mapByObject(mapFieldValue);
		return this;
	}

}
