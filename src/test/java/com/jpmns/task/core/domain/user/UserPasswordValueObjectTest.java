package com.jpmns.task.core.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.jpmns.task.core.domain.user.valueobject.UserPasswordValueObject;
import com.jpmns.task.core.domain.user.valueobject.exception.InvalidPasswordException;

class UserPasswordValueObjectTest {

    @Test
    @DisplayName("Should create a valid UserPasswordValueObject when it has exactly 8 characters")
    void shouldCreateValidPasswordWhenItHasMinLength() {
        var password = "12345678";

        var result = UserPasswordValueObject.of(password);

        assertThat(result.isFail()).isFalse();
        assertThat(result.getValue().asString()).isEqualTo(password);
    }

    @Test
    @DisplayName("Should create a valid UserPasswordValueObject when it has more than 8 characters")
    void shouldCreateValidPasswordWhenItHasMoreThanMinLength() {
        var password = "raw-password";

        var result = UserPasswordValueObject.of(password);

        assertThat(result.isFail()).isFalse();
        assertThat(result.getValue().asString()).isEqualTo(password);
    }

    @Test
    @DisplayName("Should accept an encoded bcrypt password when the value is a bcrypt hash")
    void shouldAcceptEncodedBcryptPasswordWhenValueIsBcryptHash() {
        var encoded = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        var result = UserPasswordValueObject.of(encoded);

        assertThat(result.isFail()).isFalse();
        assertThat(result.getValue().asString()).isEqualTo(encoded);
    }

    @Test
    @DisplayName("Should be equal when two instances have the same value")
    void shouldBeEqualWhenSameValue() {
        var a = UserPasswordValueObject.of("raw-password").getValue();
        var b = UserPasswordValueObject.of("raw-password").getValue();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when two instances have different values")
    void shouldNotBeEqualWhenDifferentValue() {
        var a = UserPasswordValueObject.of("raw-password").getValue();
        var b = UserPasswordValueObject.of("other-password").getValue();

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    @DisplayName("Should fail when password is null")
    void shouldFailWhenPasswordIsNull() {
        var result = UserPasswordValueObject.of(null);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getError()).isInstanceOf(InvalidPasswordException.class);
    }

    @ParameterizedTest
    @DisplayName("Should fail when password has fewer than 8 characters")
    @ValueSource(strings = {"", "1", "1234567"})
    void shouldFailWhenPasswordIsTooShort(String password) {
        var result = UserPasswordValueObject.of(password);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getError()).isInstanceOf(InvalidPasswordException.class);
    }
}
