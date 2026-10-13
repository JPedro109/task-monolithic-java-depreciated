package com.jpmns.task.core.application.usecase.user;

import org.springframework.stereotype.Service;

import com.jpmns.task.core.application.port.persistence.repository.UserRepository;
import com.jpmns.task.core.application.usecase.user.dto.input.GetUserByIdInputDto;
import com.jpmns.task.core.application.usecase.user.dto.output.UserOutputDto;
import com.jpmns.task.core.application.usecase.user.exception.UserNotFoundException;
import com.jpmns.task.core.domain.common.valueobject.IdValueObject;
import com.jpmns.task.core.domain.user.UserEntity;

@Service
public class GetUserByIdUseCase {

    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserOutputDto execute(GetUserByIdInputDto input) {
        var idValue = IdValueObject.of(input.id()).getValueOrThrow();

        var user = userRepository.findById(idValue).orElseThrow(UserNotFoundException::new);

        return toOutput(user);
    }

    private UserOutputDto toOutput(UserEntity user) {
        return new UserOutputDto(
                user.getId().asString(),
                user.getUsername().asString(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
