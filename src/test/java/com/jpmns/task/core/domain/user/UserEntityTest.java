package com.jpmns.task.core.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jpmns.task.core.domain.common.exception.DomainException;
import com.jpmns.task.shared.fixture.UserFixture;

class UserEntityTest {

    @Test
    @DisplayName("Should create a user when data is valid")
    void shouldCreateUserWhenDataIsValid() {
        var user = UserFixture.aUser();

        assertThat(user.getId().asString()).isEqualTo(UserFixture.DEFAULT_ID);
        assertThat(user.getUsername().asString()).isEqualTo(UserFixture.DEFAULT_USERNAME);
        assertThat(user.getPassword().asString()).isEqualTo(UserFixture.DEFAULT_PASSWORD);
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update the username")
    void shouldUpdateUsername() {
        var user = UserFixture.aUser();
        var username = "newusername";

        user.updateUsername(username);

        assertThat(user.getUsername().asString()).isEqualTo(username);
    }

    @Test
    @DisplayName("Should update the password")
    void shouldUpdatePassword() {
        var user = UserFixture.aUser();
        var password = "new-password";

        user.updatePassword(password);

        assertThat(user.getPassword().asString()).isEqualTo(password);
    }

    @Test
    @DisplayName("Should throw when username is too short")
    void shouldThrowWhenUsernameIsInvalid() {
        var user = UserFixture.aUser();
        var id = user.getId().asString();
        var password = user.getPassword().asString();
        var shortUsername = "ab";

        assertThatThrownBy(() -> new UserEntity(id, shortUsername, password))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when password is null")
    void shouldThrowWhenPasswordIsNull() {
        var user = UserFixture.aUser();
        var id = user.getId().asString();
        var username = user.getUsername().asString();
        String nullPassword = null;

        assertThatThrownBy(() -> new UserEntity(id, username, nullPassword))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when password has fewer than 8 characters")
    void shouldThrowWhenPasswordIsTooShort() {
        var user = UserFixture.aUser();
        var id = user.getId().asString();
        var username = user.getUsername().asString();
        var shortPassword = "1234567";

        assertThatThrownBy(() -> new UserEntity(id, username, shortPassword))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw with two errors when both username and password are invalid")
    void shouldThrowWhenBothFieldsAreInvalid() {
        var user = UserFixture.aUser();
        var id = user.getId().asString();
        var shortUsername = "ab";
        String nullPassword = null;

        assertThatThrownBy(() -> new UserEntity(id, shortUsername, nullPassword))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> {
                    var errors = ((DomainException) ex).getErrors();
                    assertThat(errors).hasSize(2);
                });
    }

    @Test
    @DisplayName("Should throw when id is null")
    void shouldThrowWhenIdIsNull() {
        var user = UserFixture.aUser();
        var username = user.getUsername().asString();
        var password = user.getPassword().asString();
        String nullId = null;

        assertThatThrownBy(() -> new UserEntity(nullId, username, password))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when id is not a valid UUID")
    void shouldThrowWhenIdIsNotUUID() {
        var user = UserFixture.aUser();
        var username = user.getUsername().asString();
        var password = user.getPassword().asString();
        var invalidId = "not-a-uuid";

        assertThatThrownBy(() -> new UserEntity(invalidId, username, password))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> {
                    var errors = ((DomainException) ex).getErrors();
                    assertThat(errors).contains("Id is not in format UUID");
                });
    }

    @Test
    @DisplayName("Should throw when updating with an invalid username")
    void shouldThrowWhenUpdatingWithInvalidUsername() {
        var user = UserFixture.aUser();
        var username = "ab";

        assertThatThrownBy(() -> user.updateUsername(username))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when updating with a null username")
    void shouldThrowWhenUpdatingWithNullUsername() {
        var user = UserFixture.aUser();
        String nullUsername = null;

        assertThatThrownBy(() -> user.updateUsername(nullUsername))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when updating with a null password")
    void shouldThrowWhenUpdatingWithNullPassword() {
        var user = UserFixture.aUser();
        String nullPassword = null;

        assertThatThrownBy(() -> user.updatePassword(nullPassword))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should throw when updating with a password shorter than 8 characters")
    void shouldThrowWhenUpdatingWithTooShortPassword() {
        var user = UserFixture.aUser();
        var shortPassword = "1234567";

        assertThatThrownBy(() -> user.updatePassword(shortPassword))
                .isInstanceOf(DomainException.class);
    }
}
