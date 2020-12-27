package chat.member;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import chat.utils.MapperUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Embeddable
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
public class MemberPrimaryKey implements Serializable {
	@Column(name = "id_chat")
	private Long idRoom;
	@Column(name = "id_user")
	private Long idUser;

	public MemberPrimaryKey mapByObject(Map<String, Object> mapFieldValue) {
		MapperUtil.mapForClass(this, this.getClass(), mapFieldValue);
		return this;
	}
}
