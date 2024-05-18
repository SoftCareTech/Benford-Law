package com.softcaretech.benfordverify;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JWTUtils {
    private static final String SECRET_KEY = "softcaretech";
    private static final String ISSUER= "SoftCareTech";

    public static String generateToken(String subject) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withSubject(subject)
                .withIssuer(ISSUER)
                .sign(algorithm);
    }

    public static DecodedJWT verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build(); // Reusable verifier instance
        return verifier.verify(token);
    }

    public static boolean verifyIssuer(String issuer) {
        return  ISSUER.equals(issuer);
    }
}
