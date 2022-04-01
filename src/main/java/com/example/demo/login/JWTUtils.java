package com.example.demo.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JWTUtils {

    /*** 过期时间一天，* 1000 * 24 * 60 * 60 一天* 1000 * 60 * 60 一小时* 15 * 60 * 1000 15分钟*/
    private static final long EXPIRE_TIME = 1000 * 24 * 60 * 60;

    /*** token私钥*/
    private static final String TOKEN_SECRET = "f26e587c28064d0e855e72c0a6a0e631";

    /*** 校验token是否正确** @param token 密钥* @return 是否正确*/
    public static boolean verify(String token) {

        try {

            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);

            JWTVerifier verifier = JWT.require(algorithm).build();

            DecodedJWT jwt = verifier.verify(token);

            return true;

        } catch (Exception exception) {

            return false;

        }
    }


    /*** 获得token中的信息无需secret解密也能获得** @return token中包含的用户名*/

    public static String getTokenStr(String token,String data) {

        try {

            DecodedJWT jwt = JWT.decode(token);

            return jwt.getClaim(data).asString();

        } catch (JWTDecodeException e) {

            return null;

        }

    }

    /*** 获取登陆用户ID* @param token* @return*/

    public static String getUserId(String token) {

        try {

            DecodedJWT jwt = JWT.decode(token);

            return jwt.getClaim("userId").asString();

        } catch (JWTDecodeException e) {

            return null;

        }

    }

    /*** 生成签名,15min后过期* @return 加密的token*/

    public static String sign(Map<String,String> map) {

        // 过期时间
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);

        // 私钥及加密算法
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);

        // 设置头部信息
        Map header = new HashMap<>(2);

        header.put("typ", "JWT");

        header.put("alg", "HS256");

        return JWT.create()

               .withHeader(header)
                //手机号
               .withClaim("mobile", map.get("mobile"))
                //姓名
                .withClaim("name",map.get("name"))
                //地址
                .withClaim("address",map.get("address"))

                // 价格代码
                .withExpiresAt(date)

               .sign(algorithm);

    }

    public static void main(String[] args) throws InterruptedException {
        Map<String, String> map = Maps.newHashMap();
        map.put("mobile","13788886666");
        map.put("name","张三");
        map.put("address","杭州");
        String token = sign(map);
        System.out.println(token);
       // TimeUnit.SECONDS.sleep(5);
        boolean verify = verify(token);
        System.out.println(verify);

        System.out.println(getTokenStr(token,"mobile"));
    }


}
