package com.nttdata.user_service.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    @InjectMocks
    private JwtProvider jwtProvider;

    private final String keyPrueba = "esta_es_una_clave_de_prueba_muy_segura_y_larga_para_el_test_123456";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtProvider, "keyString", keyPrueba);
    }

    @Test
    void createToken_ShouldReturnValidToken_WhenEmailIsProvided() {
        String email = "usuario@test.com";

        String token = jwtProvider.createToken(email);

        assertNotNull(token, "El token no debería ser nulo");
        assertFalse(token.isEmpty(), "El token no debería estar vacío");

        SecretKey key = Keys.hmacShaKeyFor(keyPrueba.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(email, claims.getSubject(), "El subject del token debe coincidir con el email");
        assertNotNull(claims.getIssuedAt(), "El token debe tener fecha de emisión");
    }
}