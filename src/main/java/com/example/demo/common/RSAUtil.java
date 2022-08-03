package com.example.demo.common;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";

    private static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz3SaNGbWaJCok/xNzxmK\n" +
            "r7IhEZYk2vUNORi6zeX8m8KFl3z4zNAPON/ISWBtNYv6E12DurnlGPG+nipv4kOf\n" +
            "Q6b9iO2ongE94idfLa+qn+5pqu3q1hyX94f/rhZ8l8pnoZ2Ns4/ZbPWrlE1yNQjM\n" +
            "22WaGBY8OA4eeoYZe7mc+eKdSeZoxHsHwp9aNbf4D03N1CWoQCdVg0hkPBBmVU2W\n" +
            "8vXhUwm6iGMSuKBKlAt4UrBSwWv6mPPXYYyBpn/UVU32yTChSq+Pe9W0oMyd3Bsy\n" +
            "T69zcNq3ICGZDCB8R27vgjJibnIyAtwkPEeKvL/bW3wWq0sNiAvPxMf9UbDHxZoP\n" +
            "UQIDAQAB";
    private static final String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDPdJo0ZtZokKiT\n" +
            "/E3PGYqvsiERliTa9Q05GLrN5fybwoWXfPjM0A8438hJYG01i/oTXYO6ueUY8b6e\n" +
            "Km/iQ59Dpv2I7aieAT3iJ18tr6qf7mmq7erWHJf3h/+uFnyXymehnY2zj9ls9auU\n" +
            "TXI1CMzbZZoYFjw4Dh56hhl7uZz54p1J5mjEewfCn1o1t/gPTc3UJahAJ1WDSGQ8\n" +
            "EGZVTZby9eFTCbqIYxK4oEqUC3hSsFLBa/qY89dhjIGmf9RVTfbJMKFKr4971bSg\n" +
            "zJ3cGzJPr3Nw2rcgIZkMIHxHbu+CMmJucjIC3CQ8R4q8v9tbfBarSw2IC8/Ex/1R\n" +
            "sMfFmg9RAgMBAAECggEBAJbai2efgDn5NxylU09Nm3O3bCSFsbcmir8CJJSksNPy\n" +
            "14IY9jK4+Ni6ocH5xHAzoV2Dms6lbtp8r8gDw+gclCt1utdfOkXrUTyqtKNcIg66\n" +
            "J0yMRT/8+uScmGfXIjKelE0JbIkuCUUaP/JPfsTh6VWAxFTPlmqMmc0uxkkZ5/Qs\n" +
            "bMRlNBndQ3NKxzC2HyN2/1huguoBXkkuXWvqKh8yl0Az/d1DFWCTJBzmccJe1LXp\n" +
            "T2KbIEl43gN5Z7qzqSJllF/ZMtE6vPYSZ1Dm3GR3wEhqO9x3wuFXQRwfdYvyxq3o\n" +
            "x8MjLrbm3R9dmp8iDYfIiFu6tu9cZkg4x/Pz99Z8SgECgYEA/gQzKy1/Ke6IsrCn\n" +
            "3SIT7Z9bTgYOkZiZNfT/DJJ2B5fDkHC3YUve8krJVJhHSuqCl8ajZij1KaSG6oz+\n" +
            "98RO6ZeQzyGWzSa1uNx9mMA6gafvsOo0xylfQfh9DcB8fvZS2q2FzXMK1sf0ps4J\n" +
            "w2lmhxP5ATI+UPZzoerKD2PMbIkCgYEA0RNSwoPwUh1pqwZjWQ86ol5mwyAS9k/z\n" +
            "WJhrx6PU/WY5Psj2J127lO7g+y3UIDsx3teo5Fh7Q9NULHUba7mzQDf0YXfL4x4p\n" +
            "7LU7/ykXdQxs1rskzVIGnRt07JixnqeNPw/7H4iGYElrohHh5xLXYDXLZPAQVWWE\n" +
            "9d7Si3XZqokCgYAl9Pi0bgL/gA5bLTyYNZxmuJZLLWqF5RiX0HlAJq6OaeYmMkGk\n" +
            "v1KcQAKfqnWUw0Aqb9tAy8P+qgAjEliTK3ljPGu+F9XR6APlkMUPy0Gy2CZf+E9U\n" +
            "p2D9maUOJgI0cphk5uMElTk8aPqs5kLdOWXBr4FY5WocbQdQXaY98Hq+iQKBgDk4\n" +
            "8bC9Tbwbwr0FXZGVgnXIIpYLJEV38mFwLieQ5o2IOei+NejmVZ3Yl4kHrQQ5T1fv\n" +
            "bJE6ylnw8BvO3Xdpdu7/oYo/sHTz34Oiws3+YCYwBWXsCJGNfrPnPOQ4knya3STC\n" +
            "affidOzLi4LrJAoLdRknAAkbT00gIPjuSG/VDIMJAoGAaYYy+BXYHoW4KtPiZLkb\n" +
            "W5oH4+mTb0XK7kjEChkGjXv3f1pk0abimrQBwYZk4HYe8l5d9Am4hE6AjgENcVic\n" +
            "lPoE7XxCEv8hnmCz79zHjqhu5IukfMK8jE/oO54L/Y3RC8eEbQ8WWl/n7V2tVLD6\n" +
            "VZTJ9SiHYaoqSstHeYEkQ8E=";

    public static void main(String[] args) {
        // 公钥加密
        String sourceStr = "{\"POS_ENTRY_MODE_CODE\":\"10\",\"AMT_TRANS\":\"1\",\"BTRACENO\":\"713405983119900673\",\"LOGIN_INST_CODE\":\"10034\",\"LOGIN_MERCH_CODE\":\"zytxmb001\",\"LOGIN_TYPE\":1,\"LOGIN_USER_CODE\":\"mcd2\",\"MCHNT_TYPE\":\"5411\",\"PRIMARY_ACCT_NUM\":\"713405983119900673\",\"RETRIVL_REF_NUM\":\"310\",\"TRACE_NO\":1000001,\"TRANS_SOURCE\":\"th\",\"TRANS_TYPE\":120270,\"VERSION\":\"4161\"}";
        System.out.println("加密前：" + sourceStr);
        String encryptStr = RSAUtil.publicEncrypt(sourceStr);
        System.out.println("加密后：" + encryptStr);
        System.out.println("长度：" + encryptStr.length());
        // 私钥解密
        String sourceStr_1 = RSAUtil.privateDecrypt(encryptStr);
        System.out.println("解密后：" + sourceStr_1);
    }

    public static String publicEncrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            //通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey.replaceAll("\r|\n", "")));
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), rsaPublicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    public static String privateDecrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            //通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.replaceAll("\r|\n", "")));
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey.replaceAll("\r|\n", "")));
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), rsaPublicKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }
}
