package chat.utils;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public final class SessionUtil implements HttpSessionListener {

	@Override
	public final void sessionCreated(HttpSessionEvent event) {

	}

	@Override
	public final void sessionDestroyed(HttpSessionEvent event) {
		System.out.println("out session");
	}
}
