package chat.dto;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;

@NamedStoredProcedureQueries(value = {
		@NamedStoredProcedureQuery(name = "SP_SENDMESSCHAT", procedureName = "SP_SENDMESSCHAT", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@CONTENT_MESS", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@TYPE_MESS", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.INOUT, type = Long.class),
				@StoredProcedureParameter(name = "@ID_SENDER", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@ID_RECIVER", mode = ParameterMode.IN, type = Long.class) }),
		@NamedStoredProcedureQuery(name = "SP_REGISTERLOGIN", procedureName = "SP_REGISTERLOGIN", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@EMAIL", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@PHONE", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@PASSWORD", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@FIRSTNAME", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@LASTNAME", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@SEX", mode = ParameterMode.IN, type = Boolean.class),
				@StoredProcedureParameter(name = "@BIRTHDAY", mode = ParameterMode.IN, type = Date.class),
				@StoredProcedureParameter(name = "@ADDRESS", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@RET", mode = ParameterMode.OUT, type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "SP_LEAVEGROUP", procedureName = "SP_LEAVEGROUP", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class) }),
		@NamedStoredProcedureQuery(name = "SP_ADDMEMBER", procedureName = "SP_ADDMEMBER", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class) }),
		@NamedStoredProcedureQuery(name = "SP_EDITNICKNAME", procedureName = "SP_EDITNICKNAME", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@ID_CURRENT", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.INOUT, type = Long.class),
				@StoredProcedureParameter(name = "@ID_PARTNER", mode = ParameterMode.INOUT, type = Long.class),
				@StoredProcedureParameter(name = "@NEW_NICKNAME", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@FULLNAME", mode = ParameterMode.INOUT, type = String.class) }),
		@NamedStoredProcedureQuery(name = "SP_CHANGEGROUPNAME", procedureName = "SP_CHANGEGROUPNAME", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@ID_CHAT", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@NEWNAME", mode = ParameterMode.IN, type = String.class) }),
		@NamedStoredProcedureQuery(name = "SP_CHANGEPASSWORD", procedureName = "SP_CHANGEPASSWORD", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@NEWPASSWORD", mode = ParameterMode.IN, type = String.class) }),
		@NamedStoredProcedureQuery(name = "SP_EDITPROFILE", procedureName = "SP_EDITPROFILE", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@PHONE_NUMBER", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@FIRST_NAME", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@LAST_NAME", mode = ParameterMode.IN, type = String.class),
				@StoredProcedureParameter(name = "@SEX", mode = ParameterMode.IN, type = Boolean.class),
				@StoredProcedureParameter(name = "@BIRTH_DAY", mode = ParameterMode.IN, type = Date.class),
				@StoredProcedureParameter(name = "@ADDRESS", mode = ParameterMode.IN, type = String.class) }),
		@NamedStoredProcedureQuery(name = "SP_UPLOAD_AVATAR", procedureName = "SP_UPLOAD_AVATAR", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@ID_USER", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@AVATAR", mode = ParameterMode.IN, type = String.class) }),
		@NamedStoredProcedureQuery(name = "SP_CHANGE_ROOMIMAGE", procedureName = "SP_CHANGE_ROOMIMAGE", resultClasses = EmptyDto.class, parameters = {
				@StoredProcedureParameter(name = "@ID_ROOM", mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(name = "@NEW_IMAGE", mode = ParameterMode.IN, type = String.class) }) })
public final class EmptyDto implements Serializable {
	public EmptyDto() {

	}
}
