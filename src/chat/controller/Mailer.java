//package chat.controller;
//
//import javax.mail.internet.MimeMessage;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//@Service("mailer")
//public class Mailer {
//	@Autowired
//	JavaMailSender mailer;
//
//	public void send(String to, String subject, String body) {
//		try {
//			MimeMessage mail = mailer.createMimeMessage();
//			MimeMessageHelper helper = new MimeMessageHelper(mail, true, "utf-8");
//			String from = "n17dccn104@student.ptithcm.edu.vn";
//			helper.setFrom(from, from);
//			helper.setTo(to);
//			helper.setReplyTo(from, from);
//			helper.setSubject(subject);
//			helper.setText(body, true);
//			mailer.send(mail);
//		} catch (Exception ex) {
//			throw new RuntimeException(ex);
//		}
//	}
//}