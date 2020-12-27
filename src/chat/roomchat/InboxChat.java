package chat.roomchat;

import javax.persistence.Entity;
import org.hibernate.annotations.DiscriminatorFormula;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
//@Table(name = "Rooms")
//@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorFormula("GroupChat")
@AllArgsConstructor
@Getter
@Setter
public class InboxChat extends Room {

}
