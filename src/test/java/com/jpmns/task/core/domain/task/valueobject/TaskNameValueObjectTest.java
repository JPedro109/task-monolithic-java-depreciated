package com.jpmns.task.core.domain.task.valueobject;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.jpmns.task.core.domain.task.valueobject.exception.InvalidTaskNameException;

class TaskNameValueObjectTest {

    @Test
    @DisplayName("Should create a valid TaskNameValueObject when the name is valid")
    void shouldCreateValidTaskNameWhenNameIsValid() {
        var name = "Buy groceries";

        var result = TaskNameValueObject.of(name);

        assertThat(result.isFail()).isFalse();
        assertThat(result.getValue().asString()).isEqualTo(name);
    }

    @Test
    @DisplayName("Should accept a name when it has exactly 255 characters")
    void shouldAcceptExactMaxLengthWhenNameHas255Characters() {
        var name = "a".repeat(255);

        var result = TaskNameValueObject.of(name);

        assertThat(result.isFail()).isFalse();
    }

    @Test
    @DisplayName("Should accept a single character name when the name has one character")
    void shouldAcceptSingleCharacterNameWhenNameHasOneCharacter() {
        var name = "X";

        var result = TaskNameValueObject.of(name);

        assertThat(result.isFail()).isFalse();
    }

    @Test
    @DisplayName("Should be equal when two instances have the same value")
    void shouldBeEqualWhenSameValue() {
        var a = TaskNameValueObject.of("Buy groceries").getValue();
        var b = TaskNameValueObject.of("Buy groceries").getValue();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when two instances have different values")
    void shouldNotBeEqualWhenDifferentValue() {
        var a = TaskNameValueObject.of("Buy groceries").getValue();
        var b = TaskNameValueObject.of("Clean the house").getValue();

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    @DisplayName("Should fail when name exceeds 255 characters")
    void shouldFailWhenNameExceedsMaxLength() {
        var name = "a".repeat(256);

        var result = TaskNameValueObject.of(name);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getError()).isInstanceOf(InvalidTaskNameException.class);
    }

    @ParameterizedTest
    @DisplayName("Should fail for null, empty or blank names")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldFailForBlankOrNullName(String name) {
        var result = TaskNameValueObject.of(name);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getError()).isInstanceOf(InvalidTaskNameException.class);
    }
}
