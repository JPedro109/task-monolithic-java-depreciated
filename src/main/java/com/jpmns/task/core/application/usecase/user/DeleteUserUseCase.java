package com.jpmns.task.core.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpmns.task.core.application.port.persistence.repository.UserRepository;
import com.jpmns.task.core.application.usecase.user.dto.input.DeleteUserInputDto;
import com.jpmns.task.core.application.usecase.user.exception.UserNotFoundException;
import com.jpmns.task.core.domain.common.valueobject.IdValueObject;

@Service
public class DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void execute(DeleteUserInputDto input) {
        var userIdValue = IdValueObject.of(input.userId()).getValueOrThrow();

        userRepository.findById(userIdValue).orElseThrow(UserNotFoundException::new);

        userRepository.deleteById(userIdValue);
    }
}
