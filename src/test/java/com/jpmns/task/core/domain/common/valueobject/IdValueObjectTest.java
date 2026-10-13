package com.jpmns.task.core.domain.common.valueobject;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.jpmns.task.core.domain.common.valueobject.exception.InvalidIdValueObjectException;

class IdValueObjectTest {

    private static final String VALID_UUID = "b2c3d4e5-f6a7-8901-bcde-f12345678901";
    private static final String VALID_UPPERCASE_UUID = "B2C3D4E5-F6A7-8901-BCDE-F12345678901";

    @Test
    @DisplayName("Should create a valid IdValueObject when the value is a UUID string")
    void shouldCreateValidIdWhenValueIsUuid() {
        var uuid = VALID_UUID;

        var result = IdValueObject.of(uuid);

        assertThat(result.isFail()).isFalse();
        assertThat(result.getValue().asString()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Should accept an uppercase UUID string when the value is a valid UUID")
    void shouldAcceptUpperCaseUuidWhenValueIsValid() {
        var uuid = VALID_UPPERCASE_UUID;

        var result = IdValueObject.of(uuid);

        assertThat(result.isFail()).isFalse();
    }

    @ParameterizedTest
    @DisplayName("Should fail for null, empty or malformed UUID strings")
    @NullAndEmptySource
    @ValueSource(strings = {
            "not-a-uuid",
            "12345678-1234-1234-1234-12345678901",
            "12345678-1234-1234-1234-1234567890123",
            "12345678_1234_1234_1234_123456789012",
            "gggggggg-gggg-gggg-gggg-gggggggggggg"
    })
    void shouldFailForInvalidId(String id) {
        var result = IdValueObject.of(id);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getError()).isInstanceOf(InvalidIdValueObjectException.class);
    }
}
