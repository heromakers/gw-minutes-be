package com.heromakers.app.minutes.common;

import lombok.Data;

@Data
public class ApiResult {
    private ResultStatus status = ResultStatus.success;

    private String reason;

    private String message;

    private String authToken;

    private String refreshToken;

    private Object data;
}