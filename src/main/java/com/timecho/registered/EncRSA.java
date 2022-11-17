package com.timecho.registered;


import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

/**
 * 非对称加密(分段加密和分段解密)
 * 服务端：私钥加密 -> 公钥解密
 * 客户端：公钥加密 -> 私钥解密
 * <p>
 * https://gitee.com/FlyLive/RSAdemo/blob/master/src/com/xiaolu/RSAUtil.java
 * https://blog.csdn.net/weixin_44774463/article/details/114095214
 */
public class EncRSA {
    private static String rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAWzhK/X+CtDhMQyUzVEQH/euInXQxbC4vXPi/HNvhBqg4p4fGN600OFWpuB3ToGgL2wOP5+JVaOUqUh7GRSEUzzPt+GGOLGujuzsT5auZ/TgJmsWUTsL555bYYp5euoPBy/KdbkjMKR8vGTITG75ngj2JhN+1etYkUlgUfuhoOQIDAQAB";
    private static String rsaPrivateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIBbOEr9f4K0OExDJTNURAf964iddDFsLi9c+L8c2+EGqDinh8Y3rTQ4Vam4HdOgaAvbA4/n4lVo5SpSHsZFIRTPM+34YY4sa6O7OxPlq5n9OAmaxZROwvnnlthinl66g8HL8p1uSMwpHy8ZMhMbvmeCPYmE37V61iRSWBR+6Gg5AgMBAAECgYBJC6CNjJX0G/ut6shQ3bOZmLdhl7l4Jshhy9cDa7j15oP1OeHau/SlsymM3Gqc9LxBgvIUAzKayIch/nnk+5JHJang4YGtH3UG892tHfZBWoq+UbBwCK8t475llCdo0AIbwEi1nbNXI8EWWFumaNCwO27IhchJw9oWchOUAaUnAQJBAMu0tScU4HB7N2Ml1iE5AY7pES6x7xIm3MMvv5GYFEXbscJrz29gwvvu00v1s2LnOVzs292yTue09dR0dwaN110CQQChTqKAAxscdbSoOQmFYqz2aEdpApxjYhTipMEMrgNZPD6WEiGHIvppUOgzsCMRh5sCEYCYBBwj9g9T+jJ3MVKNAkBVHD2MBKb5mCG+JTLgUqcaBLsPHXzbwqz+SFbsB/SAc5hDuTPEP1N4W4Kg/BllO2K2Nqhuam+ZU/xaNgb9EAppAkBqnKFUQDlD9xu+72u2iABPH57K7dnU6bdqEIAzVDOMX//xiew0985PmxTNTMXwyRKPLGg0kMrUjcWVuFBwbN3lAkB3AjD+27eicvOmT4byUyxPFLEdWwVpcoTxM3myh3EFv7Nf8D+aEJbvLK6ilIUGets6/gKiM9IrbLVVe5UZ1fB9";

    private static final String RSA_ALGORITHM_NO_PADDING = "RSA";
    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String SIGN_ALGORITHMS = "SHA256WithRSA";

    /**
     * 缺省的1024位密钥对, 可处理117个字节的数据
     */
    private static final int KEY_SIZE = 1024;
    /**
     * RSA最大加密明文大小、解密密文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String CIPHER_ENCRYPT = "encrypt";
    public static final String CIPHER_DECRYPT = "decrypt";

    /**
     * 初始化密钥
     */
    public static void initKey() throws Exception {
        KeyPairGenerator keyPairGenerator;
        keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM_NO_PADDING);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        rsaPublicKey = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        rsaPrivateKey = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
        System.out.println("rsaPublicKey = " + rsaPublicKey);
        System.out.println("rsaPrivateKey = " + rsaPrivateKey);
    }

    /**
     * 公钥获取
     */
    private static PublicKey getPublicKey() throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(rsaPublicKey));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_NO_PADDING);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        return publicKey;
    }

    /**
     * 私钥获取
     */
    private static PrivateKey getPrivateKey() throws Exception {
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(rsaPrivateKey));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_NO_PADDING);
        PrivateKey privateKey = keyFactory.generatePrivate(priPKCS8);
        return privateKey;
    }


    /**
     * 分段加密、解密
     */
    public static String section(String type, String src, Cipher cipher) throws Exception {
        if (CIPHER_ENCRYPT.equals(type)) {
            byte[] bytes = src.getBytes(DEFAULT_CHARSET);
            int inputLen = bytes.length;
            int offSet = 0;
            byte[] cache;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(bytes, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(bytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();

            return Base64.encodeBase64String(encryptedData);
        } else if (CIPHER_DECRYPT.equals(type)) {
            byte[] bytes = Base64.decodeBase64(src);
            int inputLen = bytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(bytes, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(bytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();

            return new String(decryptedData);
        }
        return "";
    }

    /**
     * 私钥加密
     */
    public static String privateEncrypt(String src) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
        return section(CIPHER_ENCRYPT, src, cipher);
    }

    /**
     * 私钥解密
     */
    public static String privateDecrypt(String src) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
        return section(CIPHER_DECRYPT, src, cipher);
    }

    /**
     * 公钥加密
     */
    public static String publicEncrypt(String src) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        return section(CIPHER_ENCRYPT, src, cipher);
    }

    /**
     * 公钥解密
     */
    public static String publicDecrypt(String src) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
        return section(CIPHER_DECRYPT, src, cipher);
    }

    /**
     * 签名（私钥）
     */
    public static String sign(String content) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(getPrivateKey());
        signature.update(content.getBytes(DEFAULT_CHARSET));
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * 验签（公钥）
     *
     * @param content
     * @param sign
     */
    public static boolean verify(String content, String sign) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initVerify(getPublicKey());
        signature.update(content.getBytes(DEFAULT_CHARSET));
        return signature.verify(Base64.decodeBase64(sign));
    }

}
