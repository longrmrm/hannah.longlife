package com.hannah.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author longrm
 * @date 2012-10-26
 */
public class BaseCoder {

	public static final String charset = "UTF-8";

	public static final String ALGORITHM_SHA = "SHA";
	public static final String ALGORITHM_MD5 = "MD5";

	/**
	 * HMAC算法可选以下多种算法
	 * 
	 * <pre>
	 * HmacMD5 
	 * HmacSHA1 
	 * HmacSHA256 
	 * HmacSHA384 
	 * HmacSHA512
	 * </pre>
	 */
	public static final String ALGORITHM_HMAC = "HmacMD5";
	public static final String ALGORITHM_AES = "AES";
	public static final String ALGORITHM_RSA = "RSA";

	public static final String ALGORITHM_SHA_RSA = "SHA1WithRSA";

	/**
	 * BASE64编码
	 * @param data
	 * @return
	 */
	public static String encodeBASE64(byte[] data) {
		return (new BASE64Encoder()).encodeBuffer(data);
	}

	/**
	 * BASE64解码
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] decodeBASE64(String data) throws IOException {
		return (new BASE64Decoder()).decodeBuffer(data);
	}

	/**
	 * MD5加密
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] encryptMD5(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance(ALGORITHM_MD5);
		md5.update(data);
		return md5.digest();
	}

	/**
	 * SHA加密
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] encryptSHA(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest sha = MessageDigest.getInstance(ALGORITHM_SHA);
		sha.update(data);
		return sha.digest();
	}

	/**
	 * HMAC加密
	 * @param data
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws IOException
	 */
	public static byte[] encryptHMAC(byte[] data, SecretKey secretKey) throws NoSuchAlgorithmException,
			InvalidKeyException, IOException {
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}

	/**
	 * 转为十六进制字符串
	 * @param messageDigest
	 * @return
	 */
	public static String toHexString(byte messageDigest[]) {
		StringBuffer hexString = new StringBuffer();
		// 字节数组转换为 十六进制 数
		for (int i = 0; i < messageDigest.length; i++) {
			String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexString.append(0);
			}
			hexString.append(shaHex);
		}
		return hexString.toString();
	}

	public static SecretKey generateSecretKey(String algorithm) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		return generateSecretKey(algorithm, 0, null);
	}

	/**
	 * 生成密钥（对称加密）
	 * @param algorithm 加密算法
	 * @param keySize
	 * @param seed
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static SecretKey generateSecretKey(String algorithm, int keySize, byte[] seed)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
		if (keySize != 0) {
			if (seed != null) {
				// linux下需强制设置RNG算法
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				random.setSeed(seed);
				keyGen.init(keySize, random);
			} else
				keyGen.init(keySize);
		}
		SecretKey secretKey = keyGen.generateKey();
		return new SecretKeySpec(secretKey.getEncoded(), secretKey.getAlgorithm());
	}

	public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return generateRSAKeyPair(0, null);
	}

	/**
	 * 生成公私钥对（非对称加密RSA）
	 * @param keySize 加密位数，默认1024
	 * @param seed
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static KeyPair generateRSAKeyPair(int keySize, byte[] seed) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
		if (keySize != 0) {
			if (seed != null) {
				// linux下需强制设置RNG算法
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				random.setSeed(seed);
				keyGen.initialize(keySize, random);
			} else
				keyGen.initialize(keySize);
		}
		return keyGen.generateKeyPair();
	}

	/**
	 * 生成RSA公钥
	 * @param publicKeyStr
	 * @return
	 * @throws Exception
	 */
	public static RSAPublicKey generateRSAPublicKey(String publicKeyStr) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodeBASE64(publicKeyStr));
		RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
		return publicKey;
	}

	/**
	 * 生成RSA私钥
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	public static RSAPrivateKey generateRSAPrivateKey(String privateKeyStr) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodeBASE64(privateKeyStr));
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
		return privateKey;
	}

	/**
	 * 加密
	 * @param data 需要加密的数据
	 * @param algorithm 加密算法
	 * @param key 加密密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, String algorithm, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	/**
	 * 解密
	 * @param data 需要解密的数据
	 * @param algorithm 解密算法
	 * @param key 解密密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, String algorithm, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	/**
	 * 用私钥对数据签名
	 * @param data
	 * @param algorithm
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] sign(byte[] data, String algorithm, PrivateKey privateKey) throws Exception {
		Signature signature = Signature.getInstance(algorithm);
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}

	/**
	 * 用公钥验证签名
	 * @param data
	 * @param signInfo 签名信息
	 * @param algorithm
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, byte[] signInfo, String algorithm, PublicKey publicKey) throws Exception {
		Signature signature = Signature.getInstance(algorithm);
		signature.initVerify(publicKey);
		signature.update(data);
		return signature.verify(signInfo);
	}

	public static void main(String[] args) throws Exception {
		byte[] data = "测试一下签名".getBytes();
		KeyPair keyPair = generateRSAKeyPair(1024, null);
		byte[] signInfo = sign(data, ALGORITHM_SHA_RSA, keyPair.getPrivate());
		System.out.println(encodeBASE64(signInfo));
		System.out.println(verify(data, signInfo, ALGORITHM_SHA_RSA, keyPair.getPublic()));
	}
}
