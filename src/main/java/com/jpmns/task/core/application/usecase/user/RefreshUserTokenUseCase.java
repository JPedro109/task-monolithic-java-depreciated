package com.jpmns.task.core.application.usecase.user;

import org.springframework.stereotype.Service;

import com.jpmns.task.core.application.port.persistence.repository.UserRepository;
import com.jpmns.task.core.application.port.security.Token;
import com.jpmns.task.core.application.usecase.user.dto.input.RefreshUserTokenInputDto;
import com.jpmns.task.core.application.usecase.user.dto.output.RefreshUserTokenOutputDto;
import com.jpmns.task.core.application.usecase.user.exception.UserNotFoundException;
import com.jpmns.task.core.domain.common.valueobject.IdValueObject;

@Service
public class RefreshUserTokenUseCase {

    private final Token token;
    private final UserRepository userRepository;

    public RefreshUserTokenUseCase(Token tokenProvider, UserRepository userRepository) {
        this.token = tokenProvider;
        this.userRepository = userRepository;
    }

    public RefreshUserTokenOutputDto execute(RefreshUserTokenInputDto input) {
        var decodeTokenDto = token.tokenValidation(input.refreshToken());

        var userIdValue = IdValueObject.of(decodeTokenDto.sub()).getValueOrThrow();

        var user = userRepository.findById(userIdValue).orElseThrow(UserNotFoundException::new);

        var newAccessToken = token.generateAccessToken(user.getId().asString());
        var newRefreshToken = token.generateRefreshToken(user.getId().asString());

        return new RefreshUserTokenOutputDto(newAccessToken, newRefreshToken);
    }
}
