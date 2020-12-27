package chat.roomchat;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
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

import javax.persistence.Entity;

@SuppressWarnings("serial")
@Entity
@Table(name = "ColorMessages")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Getter
@Setter
public class ColorMessage implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_color_message")
	@EqualsAndHashCode.Include
	private Integer idColorMessage;
	@Column(name = "start_color")
	private String startColor;
	@Column(name = "end_color")
	private String endColor;
	@Column(name = "name_color")
	private String nameColor;
	@OneToOne
    @MapsId
    @JoinColumn(name = "id_color_message")
	private Room room;

	public ColorMessage mapByObject(Map<String, Object> mapFieldValue) {
		MapperUtil.mapForClass(this, this.getClass(), mapFieldValue);
		return this;
	}
}
