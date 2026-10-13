package com.jpmns.task.core.application.usecase.task;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jpmns.task.core.application.port.persistence.repository.TaskRepository;
import com.jpmns.task.core.application.usecase.task.dto.input.CreateTaskInputDto;
import com.jpmns.task.core.application.usecase.task.dto.output.TaskOutputDto;
import com.jpmns.task.core.application.usecase.task.mapper.TaskOutputMapper;
import com.jpmns.task.core.domain.task.TaskEntity;

@Service
public class CreateTaskUseCase {

    private final TaskRepository taskRepository;

    public CreateTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskOutputDto execute(CreateTaskInputDto input) {
        var task = new TaskEntity(UUID.randomUUID().toString(), input.userId(), input.taskName(), false);
        var saved = taskRepository.save(task);

        return TaskOutputMapper.toOutput(saved);
    }
}
