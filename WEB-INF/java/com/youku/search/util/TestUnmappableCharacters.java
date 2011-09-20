package com.youku.search.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class TestUnmappableCharacters {

	public static void main(String[] args) {
		String s = "「イー・アル・カンフーで、ラップ」";
//		s = "「イーアルカンフーで、ラップ」";
		// String s = "D";

		byte[] b = encode(s.toCharArray());

		for (int i = 0; i < b.length; i++) {
			System.out.println(i + " " + b[i] + " " + (char) b[i]);
		}

		BigInteger n = new BigInteger(b);

		System.out.println(Integer.toBinaryString(n.intValue()));
	}

	public static byte[] encode(char[] input) {
		// US-ASCII
		// UTF-8
		// GB2312
		// GBK
		Charset charset = Charset.forName("GBK");
		CharsetEncoder encoder = charset.newEncoder();

		CharBuffer utfBytes = CharBuffer.wrap(input);
		ByteBuffer asciiBytes = null;

		try {
			asciiBytes = encoder.encode(utfBytes);
		} catch (CharacterCodingException e) {
			System.err.println("Error decoding: " + e + "\nStack trace:");
			e.printStackTrace();
			System.exit(-1);
		}
		return asciiBytes.array();
	}

}
