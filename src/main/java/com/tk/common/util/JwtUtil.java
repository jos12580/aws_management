package com.tk.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    /** 盐值*/
    private static final String SING="LIU!@#YISHOU@Token666";

    public static String gen(Map<String,String> userMap) {

        HashMap<String, Object> map = new HashMap<>(16);

        //获取日历对象
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, 240);

        JWTCreator.Builder builder = JWT.create();


        userMap.entrySet().stream().forEach(entry->{
            builder.withClaim(entry.getKey(),entry.getValue());
        });

        return builder
                //header,可以不写
                .withHeader(map)
                //设置过期时间
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SING));

    }

    /**
     * 验签并返回DecodedJWT
     * @param token  令牌
     */
    public  static DecodedJWT getTokenInfo(String token){
        return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }


}
