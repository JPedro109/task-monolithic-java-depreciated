package com.jpmns.task.core.domain.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jpmns.task.core.domain.common.exception.DomainException;
import com.jpmns.task.shared.fixture.TaskFixture;

class TaskEntityTest {

    @Test
    @DisplayName("Should create a task when data is valid")
    void shouldCreateTaskWhenDataIsValid() {
        var task = TaskFixture.aTask();
        var id = task.getId();
        var userId = task.getUserId();
        var taskName = task.getTaskName();

        assertThat(id).isNotNull();
        assertThat(userId).isNotNull();
        assertThat(taskName).isNotNull();
        assertThat(task.isFinished()).isFalse();
        assertThat(task.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should mark a task as finished when markAsFinished is called")
    void shouldMarkTaskAsFinishedWhenMarkAsFinishedIsCalled() {
        var task = TaskFixture.aTask();

        task.markAsFinished();

        assertThat(task.isFinished()).isTrue();
    }

    @Test
    @DisplayName("Should update the task name when the new name is valid")
    void shouldUpdateTaskNameWhenNewNameIsValid() {
        var task = TaskFixture.aTask();
        var newTaskName = "Updated task name";

        task.updateTaskName(newTaskName);

        assertThat(task.getTaskName().asString()).isEqualTo(newTaskName);
    }

    @Test
    @DisplayName("Should preserve the userId when updating the task name")
    void shouldPreserveUserIdWhenUpdatingTaskName() {
        var task = TaskFixture.aTask();
        var userId = task.getUserId();
        var newTaskName = "Updated task name";

        task.updateTaskName(newTaskName);

        assertThat(task.getUserId().asString()).isEqualTo(userId.asString());
    }

    @Test
    @DisplayName("Should throw when task name is empty")
    void shouldThrowWhenTaskNameIsEmpty() {
        var task = TaskFixture.aTask();
        var id = task.getId().asString();
        var userId = task.getUserId().asString();
        var finished = task.isFinished();
        var emptyTaskName = "";

        assertThatThrownBy(() -> new TaskEntity(id, userId, emptyTaskName, finished))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when task name is null")
    void shouldThrowWhenTaskNameIsNull() {
        var task = TaskFixture.aTask();
        var id = task.getId().asString();
        var userId = task.getUserId().asString();
        var finished = task.isFinished();
        String nullTaskName = null;

        assertThatThrownBy(() -> new TaskEntity(id, userId, nullTaskName, finished))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when task name exceeds 255 characters")
    void shouldThrowWhenTaskNameExceedsMaxLength() {
        var task = TaskFixture.aTask();
        var id = task.getId().asString();
        var userId = task.getUserId().asString();
        var finished = task.isFinished();
        var longName = "a".repeat(256);

        assertThatThrownBy(() -> new TaskEntity(id, userId, longName, finished))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when task id is not a valid UUID")
    void shouldThrowWhenIdIsNotUuid() {
        var task = TaskFixture.aTask();
        var userId = task.getUserId().asString();
        var taskName = task.getTaskName().asString();
        var finished = task.isFinished();
        var invalidTaskId = "not-a-uuid";

        assertThatThrownBy(() -> new TaskEntity(invalidTaskId, userId, taskName, finished))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> {
                    var errors = ((DomainException) ex).getErrors();
                    assertThat(errors).contains("Id is not in format UUID");
                });
    }

    @Test
    @DisplayName("Should throw when userId is not a valid UUID")
    void shouldThrowWhenUserIdIsNotUuid() {
        var task = TaskFixture.aTask();
        var id = task.getId().asString();
        var taskName = task.getTaskName().asString();
        var finished = task.isFinished();
        var invalidUserId = "not-a-uuid";

        assertThatThrownBy(() -> new TaskEntity(id, invalidUserId, taskName, finished))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when updating task name with a blank value")
    void shouldThrowWhenUpdatingWithBlankName() {
        var task = TaskFixture.aTask();
        var emptyTaskName = "";

        assertThatThrownBy(() -> task.updateTaskName(emptyTaskName))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when updating task name with null")
    void shouldThrowWhenUpdatingWithNullName() {
        var task = TaskFixture.aTask();
        String nullTaskName = null;

        assertThatThrownBy(() -> task.updateTaskName(nullTaskName))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when updating task name with a value exceeding 255 characters")
    void shouldThrowWhenUpdatingWithNameExceedingMaxLength() {
        var task = TaskFixture.aTask();
        var longName = "a".repeat(256);

        assertThatThrownBy(() -> task.updateTaskName(longName))
                .isInstanceOf(DomainException.class);
    }
}
