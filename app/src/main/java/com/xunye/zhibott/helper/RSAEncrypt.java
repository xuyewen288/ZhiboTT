package com.xunye.zhibott.helper;

import android.os.Handler;

import com.xunye.zhibott.acitvity.PayActivity;

import javax.crypto.Cipher;

import java.lang.ref.WeakReference;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAEncrypt {
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
    public static final String PublicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2voVB1sc3L6Lr7sUIRQ+v/vf8Pl1GtytDONb8mPNRN64Z1Axx39c0gv6Nin1Yz7D28D1NzRdSrdVHcgCBW5zENicv8jZ1LFP+B0+g8iwGRUwu0gNEzP1/KPQPtf2Fx5i/Bf06mg3v1FWd3Q7qQJzo9af/FXg7TEBu1Bp53+yfyQIDAQAB";
    public static final String PrivateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALa+hUHWxzcvouvuxQhFD6/+9/w+XUa3K0M41vyY81E3rhnUDHHf1zSC/o2KfVjPsPbwPU3NF1Kt1UdyAIFbnMQ2Jy/yNnUsU/4HT6DyLAZFTC7SA0TM/X8o9A+1/YXHmL8F/TqaDe/UVZ3dDupAnOj1p/8VeDtMQG7UGnnf7J/JAgMBAAECgYBF5PS+y9ECMHwV1QsTMKbhX5mlpoyygVhQq6q+jhlyFOPICSyBWWXMNdX6eN+cWkOLDzPDUA/9lXMfkmDTGSEJ3Q1HnRYAzV7HWVrSBotVuFJ3C8QvT/mmWt6uu/UfKttieoW4+gBtSsafwNPNM1wj8m7yKCq6JvB9uAhxR01gSQJBAOZD3TmAgPCkB9tF9A3uUIJprmgihGYcS75/+/y6Olzug6GOH9LiEec/KzDsjG02Id6H7jQMD4KhwwcIHm379icCQQDLKwhDxoATkydLnWH0bM9RWtFRo9m1SRrbvwt3CwJfjCvuRGDc2Xmu3udu9nM9qiZkfXfl9/GD8J0hObmsVOCPAkEAr+aHyLVxymKD3e3CUiILPpScttAndBmJgy0hwh5BF1zdET0Q8nfgVVbcF7OcUpFXrjcIsJnF/3SzF1wMYthnYQJAawt3RU52+NlVoO+BRul1qiWxl9Q+xteHwTQ9dDFmxLT0CIwahQJIrKxhQAO14E2gAN5ip9YleCD0iScC/xuRXQJAKcCu/1byxNiBb0r/16JDom6cKqpNH1NlIQrQm1DRyQXqu9V+OGWGgC3JSG7yervZu2wQF42FBVHLYqB3MgXZkw==";
    public final static String RSA_TYPE = "RSA/ECB/PKCS1Padding";
    public static void main(String[] args) throws Exception {
        //生成公钥和私钥
        genKeyPair();
        //加密字符串
        String message = "df723820";
        System.out.println("随机生成的公钥为:" + keyMap.get(0));
        System.out.println("随机生成的私钥为:" + keyMap.get(1));
        String messageEn = encrypt(message,keyMap.get(0));
        System.out.println(message + "\t加密后的字符串为:" + messageEn);
        String messageDe = decrypt(messageEn,keyMap.get(1));
        System.out.println("还原后的字符串为:" + messageDe);

        String enc=encrypt("xyw",PublicKey);
//        String enc=encrypt("xyw",keyMap.get(0));
        System.out.println("加密后的字符串为:" + enc);
//        System.out.println("还原后的字符串为:" + decrypt(enc,keyMap.get(1)));
        System.out.println("还原后的字符串为:" + decrypt(enc,PrivateKey));
    }

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encode(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encode((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance(RSA_TYPE);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encode(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance(RSA_TYPE);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }
//    private static String signSHA256RSA(String input, String strPk) throws Exception {
//        // Remove markers and new line characters in private key
//        String realPK = strPk.replaceAll( "-----END PRIVATE KEY-----", "") .replaceAll( "-----BEGIN PRIVATE KEY-----", "").replaceAll( " \n ", "");
//        byte[] b1 = Base64. getDecoder().decode(realPK.getBytes("UTF-8"));
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1); KeyFactory kf = KeyFactory. getInstance( "RSA");
//        Signature privateSignature = Signature. getInstance( "SHA256withRSA");
//        privateSignature.initSign(kf.generatePrivate(spec));
//        privateSignature.update(input.getBytes( "UTF-8"));
//        byte[] s = privateSignature.sign();
//        return Base64. getEncoder().encodeToString(s);
//    }

}


