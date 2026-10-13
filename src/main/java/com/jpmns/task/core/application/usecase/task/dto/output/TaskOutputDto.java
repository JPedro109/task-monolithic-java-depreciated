package com.jpmns.task.core.application.usecase.task.dto.output;

import java.time.Instant;

public record TaskOutputDto(String id, String userId, String taskName, boolean finished, Instant createdAt) { }
