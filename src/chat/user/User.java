package chat.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.JoinColumn;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import chat.member.Member;
import chat.utils.MapperUtil;
import chat.utils.MapperUtil.NumberButString;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("case when date_of_join is not null then 'MemberGroup' else 'MemberInbox' end")
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@NamedStoredProcedureQueries(value = {
		@NamedStoredProcedureQuery(name = "SP_GETINFORBYIDUSER", procedureName = "SP_GETINFORBYIDUSER", resultClasses = User.class, parameters = {
				@StoredProcedureParameter(name = "@ID", mode = ParameterMode.IN, type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "SP_SEARCH", procedureName = "SP_SEARCH", resultClasses = User.class, parameters = {
				@StoredProcedureParameter(name = "@SEARCH", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class) }) })
public class User implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	@EqualsAndHashCode.Include
	protected Long idUser;

	@Column(name = "avatar", columnDefinition = "NVARCHAR")
	protected String avatar;

	@NumberButString
	@NotBlank(message = "Cannot empty phone number")
	@Length(min = 10, max = 10, message = "Phone number need 10 number")
	@Column(name = "phone_number", length = 10, columnDefinition = "CHAR")
	protected String phoneNumber;

	@NotBlank(message = "Cannot empty enmail")
	@Length(max = 50, message = "Email(include @ + domain) need less 50 characters")
	@Column(name = "email", unique = true, length = 50, nullable = false)
	protected String email;

	@NotBlank(message = "Cannot empty phone first name")
	@Length(min = 10, max = 100, message = "The length of the first name should be between 10 and 100")
	@Column(name = "first_name", length = 100, columnDefinition = "NVARCHAR")
	protected String firstName;
	
	@Length(max = 100, message = "Last name need less 100 characters")
	@Column(name = "last_name", length = 100, columnDefinition = "NVARCHAR")
	protected String lastName;

	@Column(name = "sex")
	protected Boolean sex;

	@Column(name = "birth_day")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	protected Date birthDay;

	@Column(name = "address", length = 500, columnDefinition = "NVARCHAR")
	protected String address;

	@Column(name = "status")
	@DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
	protected Date status;
	
	@Column(name = "join_time")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	protected Date joinTime;

	@OneToOne
	@JoinColumn(name = "id_setting")
	protected Setting setting;

	@OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	protected List<Member> members;

	public static UserBuilder getUserBuilder() {
		return new UserBuilder();
	}

	protected void initMember() {
		if (members == null)
			members = new ArrayList<Member>();
	}

	public Member addMember(Member member) {
		initMember();
		members.add(member);
		return member;
	}

	public User mapByObject(Map<String, Object> mapFieldValue) {
		MapperUtil.mapForClass(this, this.getClass(), mapFieldValue);
		this.setting = new Setting().mapByObject(mapFieldValue);
		this.setting.setUser(this);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return idUser == ((User) obj).getIdUser();
	}
}
