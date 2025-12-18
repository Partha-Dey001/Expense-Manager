package com.partha.expensemanager.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims =  extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * GENERATE TOKEN (Method 1):
     * Accepts an email directly. Use this when you have the email string (e.g., from AuthDTO).
     */
    public String generateToken(String email) {
        // We pass the email as the "subject" (the unique user identifier)
        return buildToken(new HashMap<>(), email, jwtExpiration);
    }

    /**
     * GENERATE TOKEN (Method 2 - Optional):
     * Keeps compatibility if you still have code passing UserDetails elsewhere.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    /**
     * BUILD TOKEN (Internal Helper):
     * Constructs the actual JWT.
     */
    private String buildToken(Map<String, Object> extraClaims, String email, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(email) // The email is set as the standard "sub" claim
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey()) // Uses 0.12.x syntax
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser() // 'parserBuilder' is now just 'parser'
                .verifyWith((javax.crypto.SecretKey) getSignInKey()) // 'setSigningKey' is now 'verifyWith'
                .build()
                .parseSignedClaims(token) // 'parseClaimsJws' is now 'parseSignedClaims'
                .getPayload(); // 'getBody' is now 'getPayload'
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
