package com.jtracer.api;

public class NoHealthDataException extends RuntimeException {

    public NoHealthDataException() {
        super("No system health snapshot available yet");
    }
}
