package chat.user;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import chat.utils.MapperUtil.NumberButString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@NamedStoredProcedureQueries(value = {
		@NamedStoredProcedureQuery(name = "SP_LOGIN", procedureName = "SP_LOGIN", resultClasses = CurrentUser.class, parameters = {
				@StoredProcedureParameter(name = "@USERNAME", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@PASSWORD", mode = ParameterMode.IN, type = String.class) }) })
public final class CurrentUser extends User {
	@Column(name = "password")
	@NotBlank(message = "Cannot empty password")
	@Length(min = 6, max = 16, message = "The length of the password should be between 6 and 16")
	@NumberButString
	private String password;

	@Override
	public CurrentUser mapByObject(Map<String, Object> mapFieldValue) {
		super.mapByObject(mapFieldValue);
		return this;
	}

	public void mapByUser(User user) {
		idUser = user.idUser;
		avatar = user.avatar;
		phoneNumber = user.phoneNumber;
		email = user.email;
		firstName = user.firstName;
		lastName = user.lastName;
		sex = user.sex;
		birthDay = user.birthDay;
		address = user.address;
		status = user.status;
		joinTime = user.joinTime;
	}

	public CurrentUser registerUser() {
		avatar = avatar == "" ? null : avatar;
		if(!email.contains("@")) {
			email += "@gmail.com";
		}
		phoneNumber = phoneNumber == "" ? null : phoneNumber;
		lastName = lastName == "" ? null : lastName;
		address = address == "" ? null : address;
		return this;
	}
}
