package com.jpmns.task.core.application.usecase.user;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpmns.task.core.application.port.persistence.repository.UserRepository;
import com.jpmns.task.core.application.port.security.PasswordEncoder;
import com.jpmns.task.core.application.usecase.user.dto.input.CreateUserInputDto;
import com.jpmns.task.core.application.usecase.user.dto.output.CreateUserOutputDto;
import com.jpmns.task.core.application.usecase.user.exception.UsernameAlreadyExistsException;
import com.jpmns.task.core.domain.user.UserEntity;
import com.jpmns.task.core.domain.user.valueobject.UsernameValueObject;

@Service
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreateUserOutputDto execute(CreateUserInputDto input) {
        var username = UsernameValueObject.of(input.username()).getValueOrThrow();

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }

        var encodedPassword = passwordEncoder.encode(input.password());
        var user = new UserEntity(UUID.randomUUID().toString(), input.username(), encodedPassword);

        var saved = userRepository.save(user);

        return new CreateUserOutputDto(saved.getId().asString(), saved.getUsername().asString());
    }
}
