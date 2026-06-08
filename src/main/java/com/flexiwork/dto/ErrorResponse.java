package com.flexiwork.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse() {}

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        public Builder timestamp(LocalDateTime val) { this.timestamp = val; return this; }
        public Builder status(int val) { this.status = val; return this; }
        public Builder error(String val) { this.error = val; return this; }
        public Builder message(String val) { this.message = val; return this; }
        public Builder path(String val) { this.path = val; return this; }

        public ErrorResponse build() {
            ErrorResponse obj = new ErrorResponse();
            obj.timestamp = this.timestamp;
            obj.status = this.status;
            obj.error = this.error;
            obj.message = this.message;
            obj.path = this.path;
            return obj;
        }
    }
}
