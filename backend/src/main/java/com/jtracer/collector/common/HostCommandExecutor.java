package com.jtracer.collector.common;

import com.jtracer.collector.CommandExecutionException;

/**
 * Executes read-only host OS commands for platform collectors.
 */
public interface HostCommandExecutor {

    String execute(String... command) throws CommandExecutionException;
}
