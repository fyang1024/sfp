package com.flying;

import java.util.List;

public class ProcessException extends Exception {

    private List<String> completedHandlers;

    public ProcessException(List<String> completedHandlers, Throwable cause) {
        super(cause);
        this.completedHandlers = completedHandlers;
    }

    public List<String> getCompletedHandlers() {
        return completedHandlers;
    }
}
