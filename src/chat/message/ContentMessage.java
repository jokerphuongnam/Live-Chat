package chat.message;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonCreator;

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
@Table(name = "ContentMessages")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true )
@Builder
@Getter
@Setter
public class ContentMessage implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_content_message")
	@EqualsAndHashCode.Include
	private Long idContentMessage;
	@Column(name = "content_message", columnDefinition = "NVARCHAR")
	@NumberButString
	private String content;
	@Column(name = "type_message")
	private String type;
	
	@OneToOne
	@JoinColumn(name = "id_message")
	private Message message;

	@JsonCreator
	public ContentMessage(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
	
	public static ContentMessageBuilder ContentMessageBuilder() {
		return new ContentMessageBuilder();
	}
	
	public ContentMessage mapByObject(Map<String, Object> mapFieldValue) {
		MapperUtil.mapForClass(this, this.getClass(), mapFieldValue);
		return this;
	}
	
	public static String TEXT = "text";
	public static String VOICE = "voice";
	public static String IMAGAE = "imagae";
	public static String VIDEO = "video";
	public static String LOCATION = "location";
	public static String FILE = "file";
}
