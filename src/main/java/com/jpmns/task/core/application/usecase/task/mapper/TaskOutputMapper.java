package com.jpmns.task.core.application.usecase.task.mapper;

import com.jpmns.task.core.application.usecase.task.dto.output.TaskOutputDto;
import com.jpmns.task.core.domain.task.TaskEntity;

public final class TaskOutputMapper {

    private TaskOutputMapper() { }

    public static TaskOutputDto toOutput(TaskEntity task) {
        return new TaskOutputDto(
                task.getId().asString(),
                task.getUserId().asString(),
                task.getTaskName().asString(),
                task.isFinished(),
                task.getCreatedAt()
        );
    }
}
