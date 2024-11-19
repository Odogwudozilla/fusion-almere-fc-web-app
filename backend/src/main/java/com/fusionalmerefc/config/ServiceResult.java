package com.fusionalmerefc.config;

public class ServiceResult<T> {
    private boolean success;
    private T data;
    private ApiError apiError;

    public ServiceResult() {
        this.success = true; // Default is success
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiError getApiError() {
        return apiError;
    }

    public void setApiError(ApiError apiError) {
        this.apiError = apiError;
    }
}
