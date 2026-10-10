package dev.moduleskit.shared.exception;

import java.util.List;
import java.util.Map;

public class ValidationErrorResponse {

    private final int status;
    private final Map<String, List<String>> errors; // key = field name, value = list of messages

    public ValidationErrorResponse(int status, Map<String, List<String>> errors) {
        this.status = status;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
