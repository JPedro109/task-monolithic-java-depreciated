package com.jpmns.task.core.application.port.security.dto;

import com.jpmns.task.core.application.port.security.TokenType;

public record DecodeTokenDto(String sub, TokenType tokenType) { }
