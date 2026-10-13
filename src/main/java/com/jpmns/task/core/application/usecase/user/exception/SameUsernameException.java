package com.jpmns.task.core.application.usecase.user.exception;

public class SameUsernameException extends RuntimeException {

    public SameUsernameException() {
        super("New username must be different from the current one");
    }
}
