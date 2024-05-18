package com.softcaretech;

import java.io.UnsupportedEncodingException;

import com.auth0.jwt.JWT;
 
import com.auth0.jwt.algorithms.Algorithm; 

public class JWTUtils {
    private static final String SECRET_KEY = "softcaretech";
    private static final String ISSUER = "SoftCareTech";

    public static String generateToken(String subject) throws IllegalArgumentException, UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withSubject(subject)
                .withIssuer(ISSUER)
                .sign(algorithm);
    }

    
}
