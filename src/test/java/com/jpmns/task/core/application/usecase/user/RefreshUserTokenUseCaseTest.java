package com.jpmns.task.core.application.usecase.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jpmns.task.core.application.port.persistence.repository.UserRepository;
import com.jpmns.task.core.application.port.security.Token;
import com.jpmns.task.core.application.port.security.TokenType;
import com.jpmns.task.core.application.port.security.dto.DecodeTokenDto;
import com.jpmns.task.core.application.port.security.exception.InvalidTokenException;
import com.jpmns.task.core.application.usecase.user.dto.input.RefreshUserTokenInputDto;
import com.jpmns.task.core.application.usecase.user.exception.UserNotFoundException;
import com.jpmns.task.core.domain.common.valueobject.exception.InvalidIdValueObjectException;
import com.jpmns.task.shared.fixture.UserFixture;

@ExtendWith(MockitoExtension.class)
class RefreshUserTokenUseCaseTest {

    private static final String REFRESH_TOKEN = "refresh-token";

    @Mock
    private Token token;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshUserTokenUseCase useCase;

    @Test
    @DisplayName("Should refresh tokens successfully when refresh token is valid")
    void shouldRefreshTokensSuccessfully() {
        var newAccessToken = "new-access-token";
        var newRefreshToken = "new-refresh-token";
        var user = UserFixture.aUser();
        var userId = user.getId();
        var input = new RefreshUserTokenInputDto(REFRESH_TOKEN);

        when(token.tokenValidation(REFRESH_TOKEN)).thenReturn(new DecodeTokenDto(userId.asString(), TokenType.REFRESH));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(token.generateAccessToken(userId.asString())).thenReturn(newAccessToken);
        when(token.generateRefreshToken(userId.asString())).thenReturn(newRefreshToken);

        var output = useCase.execute(input);

        assertThat(output.accessToken()).isEqualTo(newAccessToken);
        assertThat(output.refreshToken()).isEqualTo(newRefreshToken);
        verify(token).generateAccessToken(userId.asString());
        verify(token).generateRefreshToken(userId.asString());
    }

    @Test
    @DisplayName("Should throw when token subject is not a valid user ID")
    void shouldThrowWhenTokenSubjectIsInvalidUserId() {
        var invalidUserId = "not-a-valid-uuid";
        var input = new RefreshUserTokenInputDto(REFRESH_TOKEN);

        when(token.tokenValidation(REFRESH_TOKEN)).thenReturn(new DecodeTokenDto(invalidUserId, TokenType.REFRESH));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(InvalidIdValueObjectException.class);

        verify(userRepository, never()).findById(any());
        verify(token, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Should throw when user is not found in the repository")
    void shouldThrowWhenUserNotFound() {
        var user = UserFixture.aUser();
        var userId = user.getId();
        var input = new RefreshUserTokenInputDto(REFRESH_TOKEN);

        when(token.tokenValidation(REFRESH_TOKEN)).thenReturn(new DecodeTokenDto(userId.asString(), TokenType.REFRESH));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(UserNotFoundException.class);

        verify(token, never()).generateAccessToken(any());
        verify(token, never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("Should throw when token is not a refresh token")
    void shouldThrowWhenTokenIsNotRefreshToken() {
        var user = UserFixture.aUser();
        var userId = user.getId();
        var input = new RefreshUserTokenInputDto(REFRESH_TOKEN);

        when(token.tokenValidation(REFRESH_TOKEN)).thenReturn(new DecodeTokenDto(userId.asString(), TokenType.ACCESS));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(InvalidTokenException.class);

        verify(userRepository, never()).findById(any());
        verify(token, never()).generateAccessToken(any());
        verify(token, never()).generateRefreshToken(any());
    }
}
