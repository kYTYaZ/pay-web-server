/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: CertUtils.java
 * Author:   justin
 * Date:     2014-8-6 下午2:06:06
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.common.crypto;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.pay.common.util.StringUtil;

/**
 * 证书验签工具类
 * @author justin
 */
public class CertUtils {

	private static Logger logger = LoggerFactory.getLogger(CertUtils.class);
	
    private static final String JKS = "JKS";
    private static final String P12 = "P12";
    private static final String PKCS12 = "PKCS12";
    private static final String JCEKS = "JCEKS";
    private static final String JCK = "JCK";
    private static final String PFX = "PFX";

    /**
     * 获取信息摘要
     * 
     * @param textBytes 原信息
     * @param algorithm 算法
     * @return 返回该算法的信息摘要
     * @throws NoSuchAlgorithmException 
     * @throws Exception
     */
    public static byte[] msgDigest(byte[] textBytes, String algorithm) throws NoSuchAlgorithmException  {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.update(textBytes);
        return messageDigest.digest();
    }

    /**
     * 通过证书获取公钥
     * 
     * @param certPath 证书的路径
     * @return 返回公钥
     * @throws Exception
     */
    public static PublicKey getPublicKey(String certPath) throws Exception {
        InputStream streamCert = new FileInputStream(certPath);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate cert = factory.generateCertificate(streamCert);
        return cert.getPublicKey();
    }

    /**
     * 通过密钥文件或者证书文件获取私钥
     * 
     * @param keyPath 密钥文件或者证书的路径
     * @param passwd 密钥文件或者证书的密码
     * @return 返回私钥
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String keyPath, String passwd) throws Exception {

        String keySuffix = keyPath.substring(keyPath.indexOf(".") + 1);
        String keyType = JKS;
        if (keySuffix == null || keySuffix.trim().equals(""))
            keyType = JKS;
        else
            keySuffix = keySuffix.trim().toUpperCase();

        if (keySuffix.equals(P12)) {
            keyType = PKCS12;
        } else if (keySuffix.equals(PFX)) {
            keyType = PKCS12;
        } else if (keySuffix.equals(JCK)) {
            keyType = JCEKS;
        } else
            keyType = JKS;

        return getPrivateKey(keyPath, passwd, keyType);

    }

    /**
     * 通过证书或者密钥文件获取私钥
     * 
     * @param keyPath 证书或者密钥文件
     * @param passwd 密钥保存密码
     * @param keyType 密钥保存类型
     * @return 返回私钥
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String keyPath, String passwd, String keyType) throws Exception {

        KeyStore ks = KeyStore.getInstance(keyType);
        char[] cPasswd = passwd.toCharArray();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(keyPath);
            ks.load(fis, cPasswd);
            fis.close();
        } finally {
            if (fis != null) {
                fis.close();
                fis = null;
            }
        }
        Enumeration aliasenum = ks.aliases();
        String keyAlias = null;
        PrivateKey key = null;
        while (aliasenum.hasMoreElements()) {
            keyAlias = (String) aliasenum.nextElement();
            key = (PrivateKey) ks.getKey(keyAlias, cPasswd);
            if (key != null)
                break;
        }
        return key;
    }

    /**
     * 使用私钥签名
     * 
     * @param key 私钥
     * @param b 需要签名的byte 数组
     * @return 返回签名后的byte
     * @throws Exception
     */
    public static byte[] sign(PrivateKey priKey, byte[] b) throws Exception {
        Signature sig = Signature.getInstance(priKey.getAlgorithm());
        sig.initSign(priKey);
        sig.update(b);
        return sig.sign();
    }

    /**
     * 使用公钥验证
     * 
     * @param pubKey 公钥
     * @param orgByte 原始数据byte 数组
     * @param signaByte 签名后的数据byte 数组
     * @return 是否验证结果,true验签成功、false 验签失败
     * @throws Exception
     */
    public static boolean verify(PublicKey pubKey, byte[] orgByte, byte[] signaByte) throws Exception {
        Signature sig = Signature.getInstance("SHA1withRSA");// pubKey.getAlgorithm()
//        System.out.println("-----------orgByte------------[" + new String(orgByte) + "]----");
        sig.initVerify(pubKey);
        sig.update(orgByte);
//        System.out.println("-----------orgByte------------[" + new String(orgByte) + "]----");
//        System.out.println("-----------signaByte------------[" + new String(signaByte, "UTF-8") + "]----");
        return sig.verify(signaByte);
    }

    /**
     * 使用公钥加密
     * 
     * @param pubKey 公钥
     * @param plainText 需要加密的 byte 数组
     * @return 返回加密的 byte 数据
     * @throws Exception
     */
    public static byte[] keyEncode(Key pubKey, byte[] plainText) throws Exception {
        return doFinal(pubKey, plainText, Cipher.ENCRYPT_MODE);
    }

    /**
     * 使用私钥解密
     * 
     * @param priKey 私钥
     * @param encrypText 需要解密的 byte 数组
     * @return 返回解密的 byte 数据
     * @throws Exception
     */

    public static byte[] keyDecode(Key priKey, byte[] encrypText) throws Exception {
        return doFinal(priKey, encrypText, Cipher.DECRYPT_MODE);
    }

    private static byte[] doFinal(Key key, byte[] textBytes, int MODE) throws Exception {
        // Cipher cipher = Cipher.getInstance(key.getAlgorithm(), new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(MODE, key);
        int blockSize = cipher.getBlockSize();
        int textLength = textBytes.length;

        byte[] retBytes = new byte[0];
        int loop = textLength / blockSize;
        int mod = textLength % blockSize;
        if (loop == 0)
            return cipher.doFinal(textBytes);
        for (int i = 0; i < loop; i++) {
            byte[] dstBytes = new byte[blockSize];
            System.arraycopy(textBytes, i * blockSize, dstBytes, 0, blockSize);
            byte[] encryBytes = cipher.doFinal(dstBytes);
            retBytes = StringUtil.appendArray(retBytes, encryBytes);
        }
        if (mod != 0) {
            int iPos = loop * blockSize;
            int leavingLength = textLength - iPos;
            byte[] dstBytes = new byte[leavingLength];
            System.arraycopy(textBytes, iPos, dstBytes, 0, leavingLength);
            byte[] encryBytes = cipher.doFinal(dstBytes);
            retBytes = StringUtil.appendArray(retBytes, encryBytes);
        }
        return retBytes;

    }

    public static String decodePaReq(byte[] reqByte, byte[] singByte) {
        String serverPubCert = "D:/cert/server.cer";
        String clientPriKey = "D:/cert/clientPri.pfx";
        String cliSecrete = "MTIzNDU2";
        String respXML = "";
        try {

            PublicKey pubKey = CertUtils.getPublicKey(serverPubCert);
            cliSecrete = Base64.decode(cliSecrete);
            PrivateKey priKey = CertUtils.getPrivateKey(clientPriKey, cliSecrete);

            // BASE64解码
            byte[] paReqEncryp = Base64.decode(reqByte);

            // 收到的信息摘要
            byte[] signByte = Base64.decode(singByte);

            // 收到加密信息的摘要
            byte[] orgMsgDigestByte = CertUtils.msgDigest(paReqEncryp, "MD5");

            // 验证签名
            boolean isVerify = CertUtils.verify(pubKey, orgMsgDigestByte, signByte);

            if (!isVerify) { // 如果没验证通过。返回信息
                //
                return null;
            }
            // 解密
            byte[] paReqByte = CertUtils.keyDecode(priKey, paReqEncryp);
            respXML = new String(paReqByte, "UTF8");

        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
        }
        return respXML;
    }
}
