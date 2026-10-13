package com.jpmns.task.core.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.jpmns.task.core.domain.user.valueobject.UsernameValueObject;
import com.jpmns.task.core.domain.user.valueobject.exception.InvalidUsernameException;

class UsernameValueObjectTest {

    @Test
    @DisplayName("Should create a valid UsernameValueObject when the username is valid")
    void shouldCreateValidUsernameWhenUsernameIsValid() {
        var username = "john_doe";

        var result = UsernameValueObject.of(username);

        assertThat(result.isFail()).isFalse();
        assertThat(result.getValue().asString()).isEqualTo(username);
    }

    @Test
    @DisplayName("Should accept a username when it has exactly 50 characters")
    void shouldAcceptMaximumLengthWhenUsernameHas50Characters() {
        var username = "a".repeat(50);

        var result = UsernameValueObject.of(username);

        assertThat(result.isFail()).isFalse();
    }

    @Test
    @DisplayName("Should be equal when two instances have the same value")
    void shouldBeEqualWhenSameValue() {
        var a = UsernameValueObject.of("john_doe").getValue();
        var b = UsernameValueObject.of("john_doe").getValue();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when two instances have different values")
    void shouldNotBeEqualWhenDifferentValue() {
        var a = UsernameValueObject.of("john_doe").getValue();
        var b = UsernameValueObject.of("jane_doe").getValue();

        assertThat(a).isNotEqualTo(b);
    }

    @ParameterizedTest
    @DisplayName("Should fail for null, empty, too short, too long or invalid character usernames")
    @NullAndEmptySource
    @ValueSource(strings = {
            "ab",
            "has space",
            "invalid-char!",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    void shouldFailForInvalidUsername(String username) {
        var result = UsernameValueObject.of(username);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getError()).isInstanceOf(InvalidUsernameException.class);
    }
}
