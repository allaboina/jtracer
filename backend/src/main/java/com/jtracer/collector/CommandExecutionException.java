package com.jtracer.collector;

public class CommandExecutionException extends Exception {

    private final int exitCode;

    public CommandExecutionException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
