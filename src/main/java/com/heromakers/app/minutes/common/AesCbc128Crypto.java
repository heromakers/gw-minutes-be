package com.heromakers.app.minutes.common;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;


/**
 * AES/CBC 128bit 암/복호화 서비스
 */
public class AesCbc128Crypto {

	private Cipher cipher = null;

	private static final String keyValue = "1234567890123456";
	private static SecretKeySpec secretKey = null;

	private static final byte[]  iv = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	private static IvParameterSpec ivSpec;

	private AesCbc128Crypto() {
		try {
			ivSpec = new IvParameterSpec(iv);
			secretKey = new SecretKeySpec(keyValue.getBytes(), "AES");
			cipher = Cipher.getInstance("AES/CBC/PKCS5padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	private static AesCbc128Crypto instance = null;

	public static AesCbc128Crypto getInstance() {
		if (instance == null) {
			instance = new AesCbc128Crypto();
		}
		return instance;
	}

	/**
	 * 암호화
	 * @param plainText
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String plainText) throws Exception {
		if(plainText == null || plainText.isEmpty()) return null;

		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

		return Base64.encodeBase64String(cipher.doFinal(plainText.getBytes()));
	}

	/**
	 * 복호화
	 * @param cryptText
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String cryptText) throws Exception {
		if(cryptText == null || cryptText.isEmpty()) return null;

		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

		return new String(cipher.doFinal(Base64.decodeBase64(cryptText)));
	}
	
	/**
	 * 암호화 - 별도키사용
	 * @param plainText
	 * @param keyValue
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String plainText, String keyValue) throws Exception {
		if(keyValue == null || keyValue.isEmpty()) return null;
		if(plainText == null || plainText.isEmpty()) return null;

		SecretKeySpec secretKey = new SecretKeySpec(keyValue.getBytes(), "AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

		return Base64.encodeBase64String(cipher.doFinal(plainText.getBytes()));
	}

	/**
	 * 복호화 - 별도키사용
	 * @param cryptText
	 * @param keyValue
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String cryptText, String keyValue) throws Exception {
		if(keyValue == null || keyValue.isEmpty()) return null;
		if(cryptText == null || cryptText.isEmpty()) return null;

		SecretKeySpec secretKey = new SecretKeySpec(keyValue.getBytes(), "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

		return new String(cipher.doFinal(Base64.decodeBase64(cryptText)));
	}

	/**
	 * Byte Array to Hex
	 * 
	 * @param buf
	 * @return
	 */
	public String byteArrayToHex(byte[] buf) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		for (int i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10) {
				strbuf.append("0");
			}

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	/**
	 * Hex to Byte Array
	 * 
	 * @param str
	 * @return
	 */
	public byte[] hexToByteArray(String str) {
		byte[] retValue = null;

		if (str != null && !str.isEmpty()) {
			retValue = new byte[str.length() / 2];
			for (int i = 0; i < retValue.length; i++) {
				retValue[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16);
			}
		}
		return retValue;
	}

}
