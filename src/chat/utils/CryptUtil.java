package chat.utils;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class CryptUtil {
	private static String STRING_KEY = "habsdhfadsf";

	// Mã hóa
	public static final String encrypt(Object toEncrypt) {
		return encrypt(toEncrypt, STRING_KEY);
	}

	// Giải mã
	public static final String decrypt(Object toDecrypt) {
		return decrypt(toDecrypt, STRING_KEY);
	}

	public static final String encrypt(Object toEncrypt, String stringKey) {
		String strToEncrypt = String.valueOf(toEncrypt);
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] binaryKey = stringKey.getBytes("UTF-8");
			binaryKey = sha.digest(binaryKey);
			binaryKey = Arrays.copyOf(binaryKey, 16);
			SecretKeySpec secretKey = new SecretKeySpec(binaryKey, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			return null;
		}
	}

	public static final String decrypt(Object toDecrypt, String stringKey) {
		if (toDecrypt == null)
			return null;
		String strToDecrypt = String.valueOf(toDecrypt);
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] binaryKey = stringKey.getBytes("UTF-8");
			binaryKey = sha.digest(binaryKey);
			binaryKey = Arrays.copyOf(binaryKey, 16);
			SecretKeySpec secretKey = new SecretKeySpec(binaryKey, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
}
