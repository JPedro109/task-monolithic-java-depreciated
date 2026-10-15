package com.jpmns.task.core.external.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jpmns.task.configuration.security.SecurityConfigProperties;
import com.jpmns.task.core.application.port.security.Token;
import com.jpmns.task.core.application.port.security.TokenType;
import com.jpmns.task.core.application.port.security.dto.DecodeTokenDto;
import com.jpmns.task.core.application.port.security.exception.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenAdapter implements Token {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAdapter.class);

    private static final String CLAIM_TOKEN_TYPE = "token_type";

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public TokenAdapter(SecurityConfigProperties properties) {
        var jwt = properties.jwt();
        this.secretKey = Keys.hmacShaKeyFor(jwt.secret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = jwt.accessTokenExpirationMs();
        this.refreshTokenExpirationMs = jwt.refreshTokenExpirationMs();
    }

    @Override
    public String generateAccessToken(String sub) {
        return buildToken(sub, accessTokenExpirationMs, TokenType.ACCESS);
    }

    @Override
    public String generateRefreshToken(String sub) {
        return buildToken(sub, refreshTokenExpirationMs, TokenType.REFRESH);
    }

    @Override
    public DecodeTokenDto tokenValidation(String token) {
        try {
            var claims = parseClaims(token);

            var tokenType = TokenType.fromValue(claims.get(CLAIM_TOKEN_TYPE, String.class));

            return new DecodeTokenDto(claims.getSubject(), tokenType);
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.warn("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException();
        }
    }

    private String buildToken(String sub, long expirationMs, TokenType tokenType) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(sub)
                .claim(CLAIM_TOKEN_TYPE, tokenType.getValue())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
