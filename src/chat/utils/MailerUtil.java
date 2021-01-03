package chat.utils;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailerUtil {

	@Autowired
	JavaMailSender mailer;

	public void send(String to, String subject, String body) {
		try {
			String from = "n17dccn104@student.ptithcm.edu.vn";
			MimeMessage mail = mailer.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mail, true, "utf-8");
			helper.setFrom(from, from);
			helper.setTo(to);
			helper.setReplyTo(from, from);
			helper.setSubject(subject);
			helper.setText(body, true);
			mailer.send(mail);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
