package com.jpmns.task.core.application.usecase.task;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jpmns.task.core.application.port.persistence.repository.TaskRepository;
import com.jpmns.task.core.application.usecase.task.dto.input.ListTasksInputDto;
import com.jpmns.task.core.application.usecase.task.dto.output.TaskOutputDto;
import com.jpmns.task.core.application.usecase.task.mapper.TaskOutputMapper;
import com.jpmns.task.core.domain.common.valueobject.IdValueObject;

@Service
public class ListTasksUseCase {

    private final TaskRepository taskRepository;

    public ListTasksUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskOutputDto> execute(ListTasksInputDto input) {
        var userIdValue = IdValueObject.of(input.userId()).getValueOrThrow();

        return taskRepository.findAllByUserId(userIdValue)
                .stream()
                .map(TaskOutputMapper::toOutput)
                .toList();
    }
}
