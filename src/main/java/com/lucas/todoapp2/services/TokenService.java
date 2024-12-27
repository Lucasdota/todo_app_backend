package com.lucas.todoapp2.services;
import com.auth0.jwt.interfaces.Claim;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lucas.todoapp2.entities.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    protected String secret;

    public Algorithm decodeSecret() {
        byte[] decodedSecret = Base64.getDecoder().decode(secret);
        return Algorithm.HMAC256(new String(decodedSecret));
    }

    public String generateToken(User user) {
        try {
            Algorithm algorithm = decodeSecret();
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId())
                    .withExpiresAt(LocalDateTime.now().plusHours(100).toInstant(ZoneOffset.of("-03:00")))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token: " + e.getMessage());
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = decodeSecret();
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject(); // return email
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Algorithm algorithm = decodeSecret();
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getClaim("userId")
                    .asLong(); // return user id
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid token");
        }
    }
}