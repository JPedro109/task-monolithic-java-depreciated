package com.jpmns.task.core.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpmns.task.core.application.port.persistence.repository.UserRepository;
import com.jpmns.task.core.application.port.security.PasswordEncoder;
import com.jpmns.task.core.application.usecase.user.dto.input.UpdateUserPasswordInputDto;
import com.jpmns.task.core.application.usecase.user.exception.InvalidCredentialsException;
import com.jpmns.task.core.application.usecase.user.exception.UserNotFoundException;
import com.jpmns.task.core.domain.common.valueobject.IdValueObject;

@Service
public class UpdateUserPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserPasswordUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(UpdateUserPasswordInputDto input) {
        var userIdValue = IdValueObject.of(input.userId()).getValueOrThrow();

        var user = userRepository.findById(userIdValue).orElseThrow(UserNotFoundException::new);

        var passwordIsValid = passwordEncoder.matches(input.currentPassword(), user.getPassword().asString());
        if (!passwordIsValid) {
            throw new InvalidCredentialsException();
        }

        var encodedNewPassword = passwordEncoder.encode(input.newPassword());
        user.updatePassword(encodedNewPassword);

        userRepository.save(user);
    }
}
