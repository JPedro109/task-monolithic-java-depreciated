package com.jpmns.task.core.application.usecase.user.dto.input;

public record UpdateUserPasswordInputDto(String userId, String currentPassword, String newPassword) { }
