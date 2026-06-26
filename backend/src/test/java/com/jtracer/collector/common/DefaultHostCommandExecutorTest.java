package com.jtracer.collector.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jtracer.collector.CommandExecutionException;
import org.junit.jupiter.api.Test;

class DefaultHostCommandExecutorTest {

    private final DefaultHostCommandExecutor executor = new DefaultHostCommandExecutor();

    @Test
    void drainsLargeStdoutWithoutDeadlock() throws CommandExecutionException {
        String output = executor.execute("/bin/sh", "-c", "i=0; while [ $i -lt 20000 ]; do echo line-$i; i=$((i+1)); done");
        assertTrue(output.lines().count() >= 20_000, "Expected large command output to be fully drained");
    }

    @Test
    @org.junit.jupiter.api.condition.EnabledOnOs(org.junit.jupiter.api.condition.OS.MAC)
    void executesMacPsWithoutTimingOut() throws CommandExecutionException {
        String output = executor.execute("ps", "-axo", "pid=,ppid=,pcpu=,pmem=,rss=,command=");
        assertFalse(output.isBlank());
        assertTrue(output.lines().count() > 10, "Expected ps to return multiple process rows");
    }
}
