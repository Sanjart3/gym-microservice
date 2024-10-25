package org.example.utils;

import io.jsonwebtoken.*;
import org.example.utils.exception.UnAuthorizedException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    public static final String secretKey = "!@#$mla%^&*";
    public static final int tokenLiveTime = 1000*3600; // 1 hour
    public static final int emailTokenLiveTime = 1000*3600*24;

    public static String encode(String username){
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setIssuedAt(new Date());
        jwtBuilder.signWith(SignatureAlgorithm.HS512,secretKey);

        jwtBuilder.claim("username",username);
        jwtBuilder.setExpiration(new Date(System.currentTimeMillis()+tokenLiveTime));
        jwtBuilder.setIssuer("GYM");
        return jwtBuilder.compact();
    }

    public static String decode(String token){
        try {
            JwtParser jwtParser = Jwts.parser();
            jwtParser.setSigningKey(secretKey);
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return claims.get("username").toString();
        } catch (JwtException je){
            throw new UnAuthorizedException("Your session expired");
        }
    }
}
