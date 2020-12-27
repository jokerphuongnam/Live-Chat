package chat.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import chat.user.CurrentUser;
import chat.utils.ConstanceUtil;
import chat.utils.CookieUtil;
import chat.utils.CryptUtil;
import chat.utils.JsonUtil;
import chat.utils.SqlUtil;
import chat.utils.UrlUtil;

@Controller
public final class LoginController {

	@RequestMapping(value = "login")
	private final String index(HttpSession session) {
		session.setAttribute(ConstanceUtil.ERROR_MESSAGE, null);
		return "login/Login";
	}

	@RequestMapping(value = "login", params = "loginBtn")
	private final RedirectView login(HttpServletResponse response, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		CurrentUser currentUser = SqlUtil.login(request.getParameter("usernameTxb"),
				request.getParameter("passwordTxb"));
		if (currentUser == null) {
			redirectAttributes.addFlashAttribute(ConstanceUtil.ERROR_MESSAGE, "wrong username or password");
			return new RedirectView("login");
		}
		String currentCookieString = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).endcode(currentUser);
		response.addCookie(new Cookie(ConstanceUtil.CURRENT_USER_COOKIE, currentCookieString));
		addIdCurrentRoom(response, request, currentCookieString, null);
		return new RedirectView("chat");
	}

	@RequestMapping(value = "login", params = "registerBtn")
	private final RedirectView register(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("registerUser", new CurrentUser());
		return new RedirectView("register");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final static HashMap<String, Long> getAccountLogged(HttpServletRequest request) {
		HashMap<String, Long> accountsLogged = null;
		Cookie accountsLoggedCookie = CookieUtil.getCookies(request, ConstanceUtil.ACCOUNTS_LOGGED);
		if (accountsLoggedCookie == null) {
			accountsLogged = new HashMap<String, Long>();
		} else {
			accountsLogged = new JsonUtil<HashMap>(HashMap.class, new JsonUtil.AccountsLoggedConfig())
					.decode(UrlUtil.decode(accountsLoggedCookie.getValue()));
		}
		if (accountsLogged == null) {
			accountsLogged = new HashMap<String, Long>();
		}
		return accountsLogged;
	}

	public final static Long getRoomFromAccountLogged(HttpServletRequest request) {
		HashMap<String, Long> accountLogged = getAccountLogged(request);
		if (accountLogged == null) {
			return -1L;
		}
		Long idRoom = accountLogged.get(CryptUtil
				.encrypt(new JsonUtil<CurrentUser>(CurrentUser.class, new JsonUtil.CurrentUserForCookiesConfigs())
						.decode(CookieUtil.getCookies(request, ConstanceUtil.CURRENT_USER_COOKIE).getValue())
						.getIdUser()));
		if (idRoom == null) {
			return -1L;
		}
		return idRoom;
	}

	@SuppressWarnings("rawtypes")
	public final static Long addIdCurrentRoom(HttpServletResponse response, HttpServletRequest request,
			String currentUserString, Long idCurrentRoom) {
		HashMap<String, Long> accountsLogged = getAccountLogged(request);
		if (accountsLogged.size() >= 7) {
			accountsLogged.remove(accountsLogged.keySet().iterator().next());
		}
		if (idCurrentRoom == null) {
			idCurrentRoom = Long.valueOf(-1);
		}
		CurrentUser currentUser = new JsonUtil<CurrentUser>(CurrentUser.class,
				new JsonUtil.CurrentUserForCookiesConfigs()).decode(currentUserString);
		accountsLogged.put(CryptUtil.encrypt(currentUser.getIdUser()), idCurrentRoom);
		response.addCookie(
				new Cookie(ConstanceUtil.ACCOUNTS_LOGGED, new JsonUtil<Map>(Map.class).endcode(accountsLogged)));
		return idCurrentRoom;
	}
}