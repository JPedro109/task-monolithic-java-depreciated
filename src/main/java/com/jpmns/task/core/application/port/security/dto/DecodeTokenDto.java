package com.jpmns.task.core.application.port.security.dto;

import com.jpmns.task.core.application.port.security.enums.TokenType;

public record DecodeTokenDto(String sub, TokenType tokenType) { }
