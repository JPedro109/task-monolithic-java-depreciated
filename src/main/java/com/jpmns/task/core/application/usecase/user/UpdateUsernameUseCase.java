package com.jpmns.task.core.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpmns.task.core.application.port.persistence.repository.UserRepository;
import com.jpmns.task.core.application.usecase.user.dto.input.UpdateUsernameInputDto;
import com.jpmns.task.core.application.usecase.user.dto.output.UpdateUsernameOutputDto;
import com.jpmns.task.core.application.usecase.user.exception.SameUsernameException;
import com.jpmns.task.core.application.usecase.user.exception.UserNotFoundException;
import com.jpmns.task.core.application.usecase.user.exception.UsernameAlreadyExistsException;
import com.jpmns.task.core.domain.common.valueobject.IdValueObject;

@Service
public class UpdateUsernameUseCase {

    private final UserRepository userRepository;

    public UpdateUsernameUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UpdateUsernameOutputDto execute(UpdateUsernameInputDto input) {
        var userIdValue = IdValueObject.of(input.userId()).getValueOrThrow();

        var user = userRepository.findById(userIdValue).orElseThrow(UserNotFoundException::new);

        if (user.getUsername().asString().equals(input.newUsername())) {
            throw new SameUsernameException();
        }

        user.updateUsername(input.newUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        var saved = userRepository.save(user);

        return new UpdateUsernameOutputDto(saved.getId().asString(), saved.getUsername().asString());
    }
}
