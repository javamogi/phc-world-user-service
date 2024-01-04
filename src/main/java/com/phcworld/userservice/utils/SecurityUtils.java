package com.phcworld.userservice.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {
	public static String getEncSHA256(String password) throws NoSuchAlgorithmException {
		try {
			StringBuffer sb = new StringBuffer();
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());
			byte[] str = md.digest();
			for(int i = 0; i < str.length; i++){
				byte tem = str[i];
				String tmpText = Integer.toString((tem & 0xff) + 0x100, 16).substring(1);
				sb.append(tmpText);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "error";
		}
	}
}
