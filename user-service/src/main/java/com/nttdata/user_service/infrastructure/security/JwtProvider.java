package com.nttdata.user_service.infrastructure.security;

import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {
    private final String SECRET_KEY_STRING = "una_clave_secreta_muy_larga_y_segura_para_el_ejercicio_123456";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .signWith(getSigningKey())
                .compact();
    }
}