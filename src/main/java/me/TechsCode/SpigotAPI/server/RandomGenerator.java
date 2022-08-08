/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package me.TechsCode.SpigotAPI.server;

import java.security.SecureRandom;

public class RandomGenerator {

	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();

	public static String string(int length) {
		StringBuilder sb = new StringBuilder(length);
		for(int i = 0; i < length; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public static String customString(int length, String characters) {
		StringBuilder sb = new StringBuilder(length);
		for(int i = 0; i < length; i++)
			sb.append(characters.charAt(rnd.nextInt(characters.length())));
		return sb.toString();
	}
}
