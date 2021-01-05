package chat.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import chat.user.CurrentUser;
import chat.utils.SqlUtil;
import chat.utils.SqlUtil.SQLHandler;

@Controller
public final class RegisterController {

	@RequestMapping(value = "/register")
	private final String register(ModelMap model, HttpServletRequest request) {
		CurrentUser currentUser = (CurrentUser) model.get("registerUser");
		if (currentUser == null) {
			model.addAttribute("registerUser", new CurrentUser());
		}
		return "register/Register";
	}
//
//	@Autowired
//	MailerUtil mailer;

	@RequestMapping(value = "/register", params = "registerBtn")
	private final String registerSubmit(RedirectAttributes redirectAttributes, ModelMap model,
			HttpServletRequest request, @Validated @ModelAttribute("registerUser") CurrentUser registUser,
			BindingResult errors, @ModelAttribute("repeatPassword") String repeatPassword,
			@ModelAttribute("birthDay") String birthDay) {
		boolean isError = false;
		if (!repeatPassword.equals(registUser.getPassword())) {
			isError = true;
			model.addAttribute("repeatPasswordError",
					"Password and Repeat Password should be the same");
		} else {
			model.addAttribute("repeatPasswordError", null);
		}
		if (errors.hasErrors()) {
			isError = true;
			model.addAttribute("error", "You need to enter all the information");
		} else {
			model.addAttribute("error", null);
		}
		if (birthDay.isEmpty()) {
			isError = true;
			model.addAttribute("error", "You need to enter all the information");
			model.addAttribute("birthDayError", "Can't empty birth day");
		}
		try {
			registUser.setBirthDay(new SimpleDateFormat("dd/MM/yyyy").parse(birthDay));
		} catch (ParseException e) {
			isError = true;
			redirectAttributes.addFlashAttribute("error", "Please complete and correct all information");
		}
		if (isError) {
			return "register/Register";
		} else {
//			String otp = RandomStringUtils.randomNumeric(6);
//			System.out.println(otp);
//			redirectAttributes.addFlashAttribute("emailOTP", otp);
			// mailer.send(registUser.getEmail(), SUBJECT, sendMessageOTP(otp));
			registUser.registerUser();
			return saveToDb(redirectAttributes, model, registUser);
		}
	}

	@RequestMapping(value = "/register", params = "cancelBtn")
	private final RedirectView cancelRegister() {
		return new RedirectView("login");
	}

	private final String saveToDb(RedirectAttributes redirectAttributes, ModelMap model, CurrentUser registUser) {
		System.out.println(registUser);
		AtomicReference<String> linkRedirect = new AtomicReference<String>("");
		SqlUtil.executeStoreProduce("SP_REGISTERLOGIN", new SQLHandler() {
			@Override
			protected void success(StoredProcedureQuery query) {
				super.success(query);
				System.out.println();
				setParameter("@EMAIL", registUser.getEmail(), String.class);
				setParameter("@PHONE", registUser.getPhoneNumber(), String.class, true);
				setParameter("@PASSWORD", registUser.getPassword(), String.class, true);
				setParameter("@FIRSTNAME", registUser.getFirstName(), String.class);
				setParameter("@LASTNAME", registUser.getLastName(), String.class);
				setParameter("@SEX", registUser.getSex(), Boolean.class);
				setParameter("@BIRTHDAY", registUser.getBirthDay(), Date.class);
				setParameter("@ADDRESS", registUser.getAddress(), String.class, true);
				query.registerStoredProcedureParameter("@RET", Integer.class, ParameterMode.OUT);
				query.execute();
				switch ((int) query.getOutputParameterValue("@RET")) {
				case 0:
					linkRedirect.set("login");
					model.addAttribute("error", "Register success");
					break;
				case 1:
					linkRedirect.set("register");
					model.addAttribute("error", "Email is the same");
					break;
				case 2:
					linkRedirect.set("register");
					model.addAttribute("error", "Phone number and password is the same");
					break;
				case 3:
					linkRedirect.set("register");
					model.addAttribute("error", "unknown errors");
				default:
					break;
				}
			}

			@Override
			public void error(Exception e) {
				linkRedirect.set("register");
				model.addAttribute("error", "Unknown error");
			}
		});
		if (linkRedirect.get().equals("register")) {
			return "register/Register";
		} else {
			redirectAttributes.addAttribute("error", "Register success");
			return "redirect:/login";
		}
	}

//	private final String SUBJECT = "your OTP";
//
//	private final String sendMessageOTP(String otp) {
//		return String.join(" ", "Dear you!\nCurrent your OTP:", otp);
//	}
}