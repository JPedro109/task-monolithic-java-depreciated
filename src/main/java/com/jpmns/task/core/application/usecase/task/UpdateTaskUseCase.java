package com.jpmns.task.core.application.usecase.task;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpmns.task.core.application.port.persistence.repository.TaskRepository;
import com.jpmns.task.core.application.usecase.task.dto.input.UpdateTaskInputDto;
import com.jpmns.task.core.application.usecase.task.dto.output.TaskOutputDto;
import com.jpmns.task.core.application.usecase.task.exception.TaskAccessDeniedException;
import com.jpmns.task.core.application.usecase.task.exception.TaskNotFoundException;
import com.jpmns.task.core.application.usecase.task.mapper.TaskOutputMapper;
import com.jpmns.task.core.domain.common.valueobject.IdValueObject;

@Service
public class UpdateTaskUseCase {

    private final TaskRepository taskRepository;

    public UpdateTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public TaskOutputDto execute(UpdateTaskInputDto input) {
        var taskIdValue = IdValueObject.of(input.taskId()).getValueOrThrow();

        var task = taskRepository.findById(taskIdValue).orElseThrow(TaskNotFoundException::new);

        var userIsOwnerTask = task.getUserId().asString().equals(input.userId());
        if (!userIsOwnerTask) {
            throw new TaskAccessDeniedException();
        }

        task.updateTaskName(input.taskName());
        var saved = taskRepository.save(task);

        return TaskOutputMapper.toOutput(saved);
    }
}
