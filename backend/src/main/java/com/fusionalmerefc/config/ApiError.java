package com.fusionalmerefc.config;

public class ApiError {
    private String message;
    private ApiErrorSeverity severity;

    public ApiError(String message, ApiErrorSeverity severity) {
        this.message = message;
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiErrorSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ApiErrorSeverity severity) {
        this.severity = severity;
    }
}



