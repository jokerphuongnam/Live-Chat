package chat.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class UrlUtil {
	public static final String encode(String data) {
		try {
			return URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static final String decode(String data) {
		try {
			String prevUrl = "";
			String decodeUrl = data;
			while (!prevUrl.equals(decodeUrl)) {
				prevUrl = decodeUrl;
				decodeUrl = URLDecoder.decode(decodeUrl, "UTF-8");
			}
			return decodeUrl;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
