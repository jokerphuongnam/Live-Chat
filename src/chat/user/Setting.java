package chat.user;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import chat.utils.MapperUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "Settings")
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Setting implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_setting")
	@EqualsAndHashCode.Include
	private Long idSetting;
	@Column(name = "theme", length = 20, columnDefinition = "NVARCHAR")
	private String theme;
	@Column(name = "is_show_email")
	private Boolean isShowEmail;
	@Column(name = "is_show_phone")
	private Boolean isShowPhone;
	@Column(name = "devices_logged")
	private Long devicesLogged;

	@OneToOne(mappedBy = "setting")
	private User user;

	public Setting mapByObject(Map<String, Object> mapFieldValue) {
		MapperUtil.mapForClass(this, Setting.class, mapFieldValue);
		return this;
	}
}