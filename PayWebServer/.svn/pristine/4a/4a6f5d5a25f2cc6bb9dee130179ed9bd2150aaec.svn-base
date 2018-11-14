package com.wldk.framework.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 软加密算法(DES加密) 输入：随意 输出：16位加密字符串 Modify Information: <br>
 * Author: Administrator <br>
 * Date: 2012-11-14 <br>
 * Author: Administrator <br>
 * 
 */
public class Encryption {

	private static final String Algorithm = "DESede"; // 定义 加密算法,可用

	// DES,DESede,Blowfish

	// keybyte为加密密钥，长度为24字节

	// src为被加密的数据缓冲区（源）

	private static byte[] encryptMode(byte[] keybyte, byte[] src) {

		try {
			// 生成密钥

			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

			// 加密

			Cipher c1 = Cipher.getInstance(Algorithm);

			c1.init(Cipher.ENCRYPT_MODE, deskey);

			return c1.doFinal(src);

			// 加密成功后返回

		} catch (java.security.NoSuchAlgorithmException e1) {

			e1.printStackTrace();

		} catch (javax.crypto.NoSuchPaddingException e2) {

			e2.printStackTrace();

		} catch (java.lang.Exception e3) {

			e3.printStackTrace();

		}

		return null;

		// 失败返回null

	}

	// keybyte为加密密钥，长度为24字节

	// src为加密后的缓冲区

	private static byte[] decryptMode(byte[] keybyte, byte[] src) {

		try {

			// 生成密钥

			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

			// 解密

			Cipher c1 = Cipher.getInstance(Algorithm);

			c1.init(Cipher.DECRYPT_MODE, deskey);

			return c1.doFinal(src);

		} catch (java.security.NoSuchAlgorithmException e1) {

			e1.printStackTrace();

		} catch (javax.crypto.NoSuchPaddingException e2) {

			e2.printStackTrace();

		} catch (java.lang.Exception e3) {

			e3.printStackTrace();

		}

		return null;

	}

	// 转换成十六进制字符串

	private static String byte2hex(byte[] b) {

//		String hs = "";

		String stmp = "";
		
		StringBuffer sb = new StringBuffer();
		for (int n = 0; n < b.length; n++) {

			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

			if (stmp.length() == 1)
				sb.append("0"+stmp);
//				hs = hs + "0" + stmp;

			else
				sb.append(stmp);
//				hs = hs + stmp;

			// if (n < b.length - 1)
			// hs = hs + ":";

		}

		return sb.toString().toUpperCase();

	}

	// 16进制表示的字符串转换成字节数组
	private static byte[] hex2byte(String s) throws Exception {
		char c, c1;
		int x;
		if (s.length() % 2 != 0)
			throw new Exception("密钥格式不正确");
		byte[] ret = new byte[s.length() / 2];

		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			c1 = s.charAt(++i);
			if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f'))
				throw new Exception("密钥格式不正确");
			if (!(c1 >= '0' && c1 <= '9' || c1 >= 'A' && c1 <= 'F' || c1 >= 'a' && c1 <= 'f'))
				throw new Exception("密钥格式不正确");
			x = Integer.decode("0x" + c + c1).intValue();
			if (x > 127) {
				ret[i / 2] = (byte) (x | 0xffffff00);
			} else {
				ret[i / 2] = (byte) (x);
			}
		}
		return ret;
	}

	/**
	 * 密码加密
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String src) throws Exception {
		try {
			byte[] encoded = encryptMode(hex2byte(EncrypCons.DESEDEKEY), src.getBytes());
			return byte2hex(encoded);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * 密码解密
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String src) throws Exception {

		try {

			byte[] srcByte = hex2byte(src);

			byte[] decoded = decryptMode(hex2byte(EncrypCons.DESEDEKEY), srcByte);

			return new String(decoded);

		} catch (Exception ex) {

			throw new Exception(ex.getMessage());
		}

	}

	public static void main(String[] args) {
//			System.out.println(encrypt("111111"));
//			System.out.println(decrypt("316C8910688865B1"));
	}

}