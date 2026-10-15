package com.jpmns.task.core.presentation.controller.common.handler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpmns.task.core.application.port.security.exception.InvalidTokenException;
import com.jpmns.task.core.application.usecase.task.exception.TaskAccessDeniedException;
import com.jpmns.task.core.application.usecase.user.exception.InvalidCredentialsException;
import com.jpmns.task.core.application.usecase.user.exception.SameUsernameException;
import com.jpmns.task.core.application.usecase.user.exception.UserNotFoundException;
import com.jpmns.task.core.application.usecase.user.exception.UsernameAlreadyExistsException;
import com.jpmns.task.core.domain.task.valueobject.exception.InvalidTaskNameException;
import com.jpmns.task.core.presentation.controller.common.exception.UnauthenticatedException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new ExceptionTriggerController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 422 when a DomainException is thrown")
    void shouldReturn422WhenDomainExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/domain"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("Should return 400 when the request body is malformed")
    void shouldReturn400WhenRequestBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/test/body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should return 401 when an UnauthenticatedException is thrown")
    void shouldReturn401WhenUnauthenticatedExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/unauthenticated"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should return 400 when a SameUsernameException is thrown")
    void shouldReturn400WhenSameUsernameExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/same-username"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should return 404 when a NotFoundException is thrown")
    void shouldReturn404WhenNotFoundExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should return 409 when a UsernameAlreadyExistsException is thrown")
    void shouldReturn409WhenUsernameAlreadyExistsExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Should return 403 when a TaskAccessDeniedException is thrown")
    void shouldReturn403WhenTaskAccessDeniedExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Should return 401 when an InvalidCredentialsException is thrown")
    void shouldReturn401WhenInvalidCredentialsExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/invalid-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should return 401 when an InvalidTokenException is thrown")
    void shouldReturn401WhenInvalidTokenExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should return 400 when a request field fails validation")
    void shouldReturn400WhenRequestFieldFailsValidation() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should return 500 when an unexpected exception is thrown")
    void shouldReturn500WhenUnexpectedExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @RestController
    static class ExceptionTriggerController {

        @GetMapping("/test/domain")
        void triggerDomain() {
            throw new InvalidTaskNameException();
        }

        @GetMapping("/test/unauthenticated")
        void triggerUnauthenticated() {
            throw new UnauthenticatedException("User is not authenticated");
        }

        @GetMapping("/test/same-username")
        void triggerSameUsername() {
            throw new SameUsernameException();
        }

        @GetMapping("/test/not-found")
        void triggerNotFound() {
            throw new UserNotFoundException();
        }

        @GetMapping("/test/conflict")
        void triggerConflict() {
            throw new UsernameAlreadyExistsException();
        }

        @GetMapping("/test/forbidden")
        void triggerForbidden() {
            throw new TaskAccessDeniedException();
        }

        @GetMapping("/test/invalid-credentials")
        void triggerInvalidCredentials() {
            throw new InvalidCredentialsException();
        }

        @GetMapping("/test/invalid-token")
        void triggerInvalidToken() {
            throw new InvalidTokenException();
        }

        @GetMapping("/test/generic")
        void triggerGeneric() {
            throw new IllegalStateException("Unexpected failure");
        }

        @PostMapping("/test/body")
        void triggerBody(@RequestBody TestPayload payload) {
            throw new IllegalStateException("Should not reach handler for a malformed body: " + payload);
        }

        @PostMapping("/test/validation")
        void triggerValidation(@Valid @RequestBody ValidatedPayload payload) {
            throw new IllegalStateException("Should not reach handler for an invalid body: " + payload);
        }
    }

    record TestPayload(String value) { }

    record ValidatedPayload(@NotBlank String name) { }
}
