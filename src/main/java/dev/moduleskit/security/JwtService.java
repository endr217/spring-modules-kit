package dev.moduleskit.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.moduleskit.modules.users.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "spring-api";
    public static final String TOKEN_AUDIENCE = "nextjs-app";

    private final SecretKey secretKey;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    public JwtService(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(User user) {
        return Jwts.builder()
                .header().add("typ", TOKEN_TYPE)
                .and()
                .issuer(TOKEN_ISSUER)
                .audience().add(TOKEN_AUDIENCE).and()
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .signWith(secretKey, SIG.HS512)
                .compact();
    }

    public Optional<Jws<Claims>> validateTokenAndGetJws(String authToken) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(authToken);
            return Optional.of(jws);
        } catch (ExpiredJwtException exception) {
            System.err.println("JWT expired: " + exception.getMessage());
        } catch (JwtException exception) {
            System.err.println("Invalid JWT: " + exception.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Generate a short-lived registration token used to complete OAuth-created accounts.
     */
    public String generateRegistrationToken(User user) {
        long shortValidity = 15 * 60; // 15 minutes
        return Jwts.builder()
                .header().add("typ", TOKEN_TYPE)
                .and()
                .issuer(TOKEN_ISSUER)
                .audience().add(TOKEN_AUDIENCE).and()
                .expiration(new Date(System.currentTimeMillis() + shortValidity * 1000))
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("registration", true)
                .signWith(secretKey, SIG.HS512)
                .compact();
    }
}
