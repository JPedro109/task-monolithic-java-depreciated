package com.jpmns.task.core.presentation.controller.common.resolver;

import org.springframework.security.core.context.SecurityContextHolder;

import com.jpmns.task.core.presentation.controller.common.exception.UnauthenticatedException;

public final class AuthenticatedUserResolver {

    private AuthenticatedUserResolver() { }

    public static String getUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        var principal = authentication.getPrincipal();
        if (principal == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        return principal.toString();
    }

    public static String getUserIdOrNull() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        var principal = authentication.getPrincipal();
        if (principal == null) {
            return null;
        }

        return principal.toString();
    }
}
