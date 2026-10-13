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

import com.jpmns.task.core.application.usecase.user.exception.SameUsernameException;
import com.jpmns.task.core.domain.task.valueobject.exception.InvalidTaskNameException;
import com.jpmns.task.core.presentation.controller.common.exception.UnauthenticatedException;

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

        @GetMapping("/test/generic")
        void triggerGeneric() {
            throw new IllegalStateException("Unexpected failure");
        }

        @PostMapping("/test/body")
        void triggerBody(@RequestBody TestPayload payload) {
            throw new IllegalStateException("Should not reach handler for a malformed body: " + payload);
        }
    }

    record TestPayload(String value) { }
}
