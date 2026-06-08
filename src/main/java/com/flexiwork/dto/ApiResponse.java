package com.flexiwork.dto;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.message = message;
        return r;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.message = message;
        return r;
    }

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;

        public Builder<T> success(boolean val) { this.success = val; return this; }
        public Builder<T> message(String val) { this.message = val; return this; }
        public Builder<T> data(T val) { this.data = val; return this; }

        public ApiResponse<T> build() {
            ApiResponse<T> obj = new ApiResponse<>();
            obj.success = this.success;
            obj.message = this.message;
            obj.data = this.data;
            return obj;
        }
    }
}
