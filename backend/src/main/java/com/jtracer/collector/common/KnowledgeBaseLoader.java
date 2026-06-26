package com.jtracer.collector.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeBaseLoader {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseLoader.class);

    private final ObjectMapper objectMapper;
    private final String basePath;

    public KnowledgeBaseLoader(ObjectMapper objectMapper, com.jtracer.config.JtracerProperties properties) {
        this.objectMapper = objectMapper;
        this.basePath = properties.getKnowledgeBase().getBasePath();
    }

    public DeviceIdentityKnowledgeBase load() {
        try {
            Map<String, String> oui = readOuiVendors();
            List<DeviceIdentityRuleDefinition> rules = readRules();
            List<MdnsServiceDefinition> mdns = readMdnsServices();
            log.info(
                    "Loaded device identity knowledge base: {} OUI entries, {} rules, {} mDNS services",
                    oui.size(),
                    rules.size(),
                    mdns.size());
            return new DeviceIdentityKnowledgeBase(oui, rules, mdns);
        } catch (IOException ex) {
            log.warn("Failed to load device identity knowledge base from {}: {}", basePath, ex.getMessage());
            return DeviceIdentityKnowledgeBase.empty();
        }
    }

    private Map<String, String> readOuiVendors() throws IOException {
        JsonNode root = readJson("oui-vendors.json");
        Map<String, Map<String, String>> raw =
                objectMapper.convertValue(root, new TypeReference<Map<String, Map<String, String>>>() {});
        return raw.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        e -> e.getKey().toLowerCase(),
                        e -> e.getValue().get("vendor"),
                        (a, b) -> a));
    }

    private List<DeviceIdentityRuleDefinition> readRules() throws IOException {
        JsonNode root = readJson("device-rules.json");
        DeviceRulesFile file = objectMapper.treeToValue(root, DeviceRulesFile.class);
        if (file.getRules() == null) {
            return List.of();
        }
        return file.getRules().stream()
                .sorted(Comparator.comparingInt(DeviceIdentityRuleDefinition::getPriority).reversed())
                .toList();
    }

    private List<MdnsServiceDefinition> readMdnsServices() throws IOException {
        JsonNode root = readJson("mdns-services.json");
        MdnsServicesFile file = objectMapper.treeToValue(root, MdnsServicesFile.class);
        return file.getServices() != null ? file.getServices() : List.of();
    }

    private JsonNode readJson(String fileName) throws IOException {
        if (basePath.startsWith("classpath:")) {
            String resourcePath = basePath.substring("classpath:".length()) + "/" + fileName;
            if (resourcePath.startsWith("/")) {
                resourcePath = resourcePath.substring(1);
            }
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                if (in == null) {
                    throw new IOException("Classpath resource not found: " + resourcePath);
                }
                return objectMapper.readTree(in);
            }
        }
        Path path = Path.of(basePath).resolve(fileName);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + path);
        }
        return objectMapper.readTree(Files.readString(path));
    }

    private static class DeviceRulesFile {
        private List<DeviceIdentityRuleDefinition> rules;

        public List<DeviceIdentityRuleDefinition> getRules() {
            return rules;
        }

        public void setRules(List<DeviceIdentityRuleDefinition> rules) {
            this.rules = rules;
        }
    }

    private static class MdnsServicesFile {
        private List<MdnsServiceDefinition> services;

        public List<MdnsServiceDefinition> getServices() {
            return services;
        }

        public void setServices(List<MdnsServiceDefinition> services) {
            this.services = services;
        }
    }
}
