package com.jpmns.task.shared.fixture;

import com.jpmns.task.core.domain.user.UserEntity;

public final class UserFixture {

    public static final String DEFAULT_ID = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
    public static final String DEFAULT_USERNAME = "john_doe";
    public static final String DEFAULT_PASSWORD = "password";

    private UserFixture() { }

    public static UserEntity aUser() {
        return new UserEntity(DEFAULT_ID, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static UserEntity aUserWithUsername(String username) {
        return new UserEntity(DEFAULT_ID, username, DEFAULT_PASSWORD);
    }
}
