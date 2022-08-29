package com.crawler.api.excep;

public class TaskException extends RuntimeException {

    public TaskException() {
        super();
    }

    public TaskException(String message) {
        super(message);
    }
}
