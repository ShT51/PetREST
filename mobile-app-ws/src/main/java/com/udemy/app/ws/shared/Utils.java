package com.udemy.app.ws.shared;

import com.udemy.app.ws.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Service
public class Utils {
    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateUserId(int length) {
        return generateRandomString(length);
    }

    public String generateAddressId(int length) {
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }

    /**
     * Check token's expiration date.
     * Jwts parse it and then compare token's expiration date with current date
     *
     * @param token
     * @return true if token has expired, false if it hasn't
     */
    public static boolean hasTokenExpired(String token) {
        boolean returnValue = false;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token).getBody();

            Date tokenExpirationDate = claims.getExpiration();
            Date currentDate = new Date();
            returnValue = tokenExpirationDate.before(currentDate);
        } catch (ExpiredJwtException ex) {
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * Generate email verification token
     */
    public String generateEmailVerificationToken(String publicUserId) {
        return generateToken(publicUserId, SecurityConstants.EXPIRATION_TIME);
    }

    /**
     * Generate password reset token
     */
    public String generatePasswordResetToken(String userId) {
        return generateToken(userId, SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME);
    }

    /**
     * Generate tokens using Jwts library
     *
     * @param subject to generate toke
     * @param expirationTime of token
     * @return generated token
     */
    private String generateToken(String subject, Long expirationTime) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
    }
}
