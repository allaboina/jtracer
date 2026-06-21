package com.jtracer.config;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseDirectoryConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseDirectoryConfig.class);

    private final JtracerProperties properties;

    public DatabaseDirectoryConfig(JtracerProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void ensureDataDirectoriesExist() throws IOException {
        createParentDirectory(properties.getDatabase().getLivePath());
        createParentDirectory(properties.getDatabase().getDemoPath());
    }

    private void createParentDirectory(String dbPath) throws IOException {
        if (dbPath.startsWith("file:") || dbPath.contains("mode=memory")) {
            return;
        }
        Path path = Path.of(dbPath).toAbsolutePath().getParent();
        if (path != null && !Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Created database directory: {}", path);
        }
    }
}
