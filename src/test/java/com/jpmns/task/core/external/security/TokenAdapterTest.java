package com.jpmns.task.core.external.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jpmns.task.configuration.security.SecurityConfigProperties;
import com.jpmns.task.core.application.port.security.TokenType;
import com.jpmns.task.core.application.port.security.exception.InvalidTokenException;
import com.jpmns.task.shared.fixture.UserFixture;

class TokenAdapterTest {

    private static final String SECRET = "test-secret-key-must-be-at-least-32-chars!!";
    private static final long ACCESS_EXPIRATION_MS = 900_000L;
    private static final long REFRESH_EXPIRATION_MS = 604_800_000L;

    private TokenAdapter tokenAdapter;

    @BeforeEach
    void setUp() {
        tokenAdapter = new TokenAdapter(buildProperties(SECRET, ACCESS_EXPIRATION_MS, REFRESH_EXPIRATION_MS));
    }

    @Test
    @DisplayName("Should generate a non-null access token when a subject is given")
    void shouldGenerateAccessTokenWhenSubjectIsGiven() {
        var user = UserFixture.aUser();
        var sub = user.getId().asString();

        var token = tokenAdapter.generateAccessToken(sub);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("Should generate a non-null refresh token when a subject is given")
    void shouldGenerateRefreshTokenWhenSubjectIsGiven() {
        var user = UserFixture.aUser();
        var sub = user.getId().asString();

        var token = tokenAdapter.generateRefreshToken(sub);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("Should generate different tokens when generating access and refresh for the same subject")
    void shouldGenerateDifferentTokensWhenGeneratingAccessAndRefresh() {
        var user = UserFixture.aUser();
        var sub = user.getId().asString();

        var accessToken = tokenAdapter.generateAccessToken(sub);
        var refreshToken = tokenAdapter.generateRefreshToken(sub);

        assertThat(accessToken).isNotEqualTo(refreshToken);
    }

    @Test
    @DisplayName("Should validate a valid access token and return the correct subject")
    void shouldValidateAccessTokenAndReturnSubject() {
        var user = UserFixture.aUser();
        var sub = user.getId().asString();
        var token = tokenAdapter.generateAccessToken(sub);

        var decoded = tokenAdapter.tokenValidation(token);

        assertThat(decoded).isNotNull();
        assertThat(decoded.sub()).isEqualTo(sub);
    }

    @Test
    @DisplayName("Should surface the ACCESS token type when validating an access token")
    void shouldSurfaceAccessTokenTypeWhenValidatingAccessToken() {
        var user = UserFixture.aUser();
        var sub = user.getId().asString();
        var token = tokenAdapter.generateAccessToken(sub);

        var decoded = tokenAdapter.tokenValidation(token);

        assertThat(decoded.tokenType()).isEqualTo(TokenType.ACCESS);
    }

    @Test
    @DisplayName("Should validate a valid refresh token and return the correct subject")
    void shouldValidateRefreshTokenAndReturnSubject() {
        var user = UserFixture.aUser();
        var sub = user.getId().asString();
        var token = tokenAdapter.generateRefreshToken(sub);

        var decoded = tokenAdapter.tokenValidation(token);

        assertThat(decoded).isNotNull();
        assertThat(decoded.sub()).isEqualTo(sub);
    }

    @Test
    @DisplayName("Should surface the REFRESH token type when validating a refresh token")
    void shouldSurfaceRefreshTokenTypeWhenValidatingRefreshToken() {
        var user = UserFixture.aUser();
        var sub = user.getId().asString();
        var token = tokenAdapter.generateRefreshToken(sub);

        var decoded = tokenAdapter.tokenValidation(token);

        assertThat(decoded.tokenType()).isEqualTo(TokenType.REFRESH);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token is malformed")
    void shouldThrowWhenTokenIsMalformed() {
        var malformed = "this.is.not.a.valid.jwt";

        assertThatThrownBy(() -> tokenAdapter.tokenValidation(malformed))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token is empty")
    void shouldThrowWhenTokenIsEmpty() {
        assertThatThrownBy(() -> tokenAdapter.tokenValidation(""))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token is signed with a different secret")
    void shouldThrowWhenTokenSignedWithDifferentSecret() {
        var properties = buildProperties("another-secret-key-must-be-at-least-32-chars!",
                    ACCESS_EXPIRATION_MS,
                    REFRESH_EXPIRATION_MS);
        var otherAdapter = new TokenAdapter(properties);
        var user = UserFixture.aUser();
        var sub = user.getId().asString();
        var token = otherAdapter.generateAccessToken(sub);

        assertThatThrownBy(() -> tokenAdapter.tokenValidation(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token is expired")
    void shouldThrowWhenTokenIsExpired() {
        var expiredAdapter = new TokenAdapter(buildProperties(SECRET, -1L, -1L));
        var user = UserFixture.aUser();
        var sub = user.getId().asString();
        var token = expiredAdapter.generateAccessToken(sub);

        assertThatThrownBy(() -> tokenAdapter.tokenValidation(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    private SecurityConfigProperties buildProperties(String secret, long accessMs, long refreshMs) {
        return new SecurityConfigProperties(new SecurityConfigProperties.Jwt(secret, accessMs, refreshMs));
    }
}
