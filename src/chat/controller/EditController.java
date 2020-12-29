package chat.controller;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import chat.user.CurrentUser;
import chat.utils.ConstanceUtil;
import chat.utils.JsonUtil;
import chat.utils.SqlUtil;
import chat.utils.SqlUtil.SQLHandler;

@Controller
public class EditController {
	public static String rootPath = "chat/edit/";

	@RequestMapping(value = "editprofile", method = RequestMethod.GET)
	private final String profile(ModelMap model,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		model.addAttribute("newProfile", SqlUtil.login(currentUser.getEmail(), currentUser.getPassword()));
		return rootPath + "profile";
	}

	@RequestMapping(value = "editprofile", params = "editProfileBtn")
	private final String editProfileConfirm(Model model,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson,
			@Validated @ModelAttribute("newProfile") CurrentUser newProfile, BindingResult errors) {
		AtomicReference<Boolean> isSuccess = new AtomicReference<Boolean>(true);
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		if (errors.getErrorCount() < 1) {
			model.addAttribute("error", "Please complete and correct all information");
			isSuccess.set(false);
		}
		if (isSuccess.get()) {
			SqlUtil.executeStoreProduce("SP_EDITPROFILE", new SQLHandler() {
				@Override
				protected void success(StoredProcedureQuery query) {
					super.success(query);
					newProfile.registerUser();
					setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
					setParameter("@PHONE_NUMBER", newProfile.getPhoneNumber(), String.class);
					setParameter("@FIRST_NAME", newProfile.getFirstName(), String.class);
					setParameter("@LAST_NAME", newProfile.getLastName(), String.class, true);
					setParameter("@SEX", newProfile.getSex(), Boolean.class);
					setParameter("@BIRTH_DAY", newProfile.getBirthDay(), Date.class);
					setParameter("@ADDRESS", newProfile.getAddress(), String.class, true);
					query.execute();
				}

				@Override
				public void error(Exception e) {
					e.printStackTrace();
					model.addAttribute("error", "Unkown error");
					isSuccess.set(false);
				}
			});
			if (isSuccess.get()) {
				return "redirect:/chat";
			} else {
				return rootPath + "profile";
			}
		} else {
			return rootPath + "profile";
		}
	}

	@RequestMapping(value = "editprofile", params = "cancelBtn")
	private final RedirectView cancelEditProfile() {
		return new RedirectView("chat");
	}

	@RequestMapping(value = "editpassword", method = RequestMethod.GET)
	private final String password(ModelMap model,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		CurrentUser fullCurrentUser = SqlUtil.login(currentUser.getEmail(), currentUser.getPassword());
		currentUser.setPassword("");
		String fullName = fullCurrentUser.getFirstName() + fullCurrentUser.getLastName() == null ? ""
				: " " + fullCurrentUser.getLastName();
		model.addAttribute("oldAvatar", fullCurrentUser.getAvatar());
		model.addAttribute("fullName", fullCurrentUser.getFirstName() + fullName);
		model.addAttribute("newPasswordUser", new CurrentUser());
		return rootPath + "password";
	}

	@RequestMapping(value = "editpassword", params = "changePasswordBtn")
	private final String changePasswordSubmit(ModelMap model,
			@CookieValue(ConstanceUtil.CURRENT_USER_COOKIE) String currentUserJson, HttpServletRequest request,
			@ModelAttribute("oldPassword") String oldPassword, @ModelAttribute("repeatPassword") String repeatPassword,
			@Validated @ModelAttribute("newPasswordUser") CurrentUser newPassword, BindingResult errors,
			HttpServletResponse response) {
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserJson);
		boolean isErrors = false;
		if (!currentUser.getPassword().equals(oldPassword)) {
			isErrors = true;
			model.addAttribute("oldPasswordError", "Wrong current password");
		}
		if (errors.getErrorCount() < 3) {
			currentUser.setPassword("");
			model.addAttribute("newPasswordUser", currentUser);
			isErrors = true;
		}
		if (!newPassword.getPassword().equals(repeatPassword)) {
			isErrors = true;
			model.addAttribute("repeatPasswordError", "New password and repeat password not the same");
		}
		if (isErrors) {
			return rootPath + "password";
		} else {
			SqlUtil.executeStoreProduce("SP_CHANGEPASSWORD", new SQLHandler() {
				@Override
				protected void success(StoredProcedureQuery query) {
					super.success(query);
					setParameter("@ID_USER", currentUser.getIdUser(), Long.class);
					setParameter("@NEWPASSWORD", newPassword.getPassword(), String.class);
					query.execute();
					currentUser.setPassword(newPassword.getPassword());
					String currentCookieString = new JsonUtil<CurrentUser>(CurrentUser.class,
							new JsonUtil.CurrentUserForCookiesConfigs()).endcode(currentUser);
					response.addCookie(new Cookie(ConstanceUtil.CURRENT_USER_COOKIE, currentCookieString));
				}

				@Override
				public void error(Exception e) {
					e.printStackTrace();
				}
			});
			model.addAttribute("oldPassword", null);
			model.addAttribute("repeatPassword", null);
			return "redirect:/chat";
		}
	}

	@RequestMapping(value = "editpassword", params = "cancelBtn")
	private final RedirectView cancelChangePassword() {
		return new RedirectView("chat");
	}
}