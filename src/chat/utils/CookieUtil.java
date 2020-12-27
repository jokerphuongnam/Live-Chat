package chat.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public final class CookieUtil {

	public static final Cookie getCookies(HttpServletRequest request, String nameCookie) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().toLowerCase().equals(nameCookie.toLowerCase())) {
				return cookie;
			}
		}
		return null;
	}
}