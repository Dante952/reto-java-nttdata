package com.nttdata.user_service.infrastructure.security;

import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${security.jwt.secret}")
    private String keyString;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(keyString.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .signWith(getSigningKey())
                .compact();
    }
}