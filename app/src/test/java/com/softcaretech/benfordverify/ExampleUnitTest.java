package com.softcaretech.benfordverify;

import org.junit.Test;

import static org.junit.Assert.*;


import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testKeyPass() {
        String msg = "user123";
        String token = JWTUtils.generateToken(msg);
        DecodedJWT decodedJWT = JWTUtils.verifyToken(token);
        assertEquals(decodedJWT.getSubject(), msg);
        assertEquals(decodedJWT.getIssuer(), "SoftCareTech");

    }


    public void testKeyFailedSubject() {
        String msg = "user123";
        String token = JWTUtils.generateToken(msg);
        DecodedJWT decodedJWT = JWTUtils.verifyToken(token);
        assertEquals(decodedJWT.getSubject(), "msg");
    }
    public void testKeyFailedIssuer() {
        String msg = "user123";
        String token = JWTUtils.generateToken(msg);
        DecodedJWT decodedJWT = JWTUtils.verifyToken(token);
        assertEquals(decodedJWT.getIssuer(), "Ter");
    }
}