package com.jpmns.task.core.presentation.controller.payload.user.response;

import com.jpmns.task.core.application.usecase.user.dto.output.UserLoginOutputDto;
import com.jpmns.task.core.presentation.controller.documentation.payload.user.response.UserLoginResponseDoc;

public record UserLoginResponse(
        String accessToken,
        String refreshToken
) implements UserLoginResponseDoc {

    public static UserLoginResponse of(UserLoginOutputDto dto) {
        return new UserLoginResponse(dto.accessToken(), dto.refreshToken());
    }

    @Override
    public String toString() {
        return "UserLoginResponse{accessToken='[PROTECTED]', refreshToken='[PROTECTED]'}";
    }
}
