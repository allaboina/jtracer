package com.jtracer.config;

import com.jtracer.domain.enums.CaptureMode;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jtracer")
public class JtracerProperties {

    private String appVersion = "0.1.0-SNAPSHOT";
    private CaptureMode captureMode = CaptureMode.LIVE;
    private final Database database = new Database();
    private final Retention retention = new Retention();
    private final Polling polling = new Polling();
    private final KnowledgeBase knowledgeBase = new KnowledgeBase();
    private final Network network = new Network();

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public CaptureMode getCaptureMode() {
        return captureMode;
    }

    public void setCaptureMode(CaptureMode captureMode) {
        this.captureMode = captureMode;
    }

    public Database getDatabase() {
        return database;
    }

    public Retention getRetention() {
        return retention;
    }

    public Polling getPolling() {
        return polling;
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public Network getNetwork() {
        return network;
    }

    public static class Database {

        private String livePath = "./data/jtracer-live.db";
        private String demoPath = "./data/jtracer-demo.db";

        public String getLivePath() {
            return livePath;
        }

        public void setLivePath(String livePath) {
            this.livePath = livePath;
        }

        public String getDemoPath() {
            return demoPath;
        }

        public void setDemoPath(String demoPath) {
            this.demoPath = demoPath;
        }
    }

    public static class Retention {

        private int days = 7;
        private List<Integer> allowedDays = List.of(1, 7, 30);

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public List<Integer> getAllowedDays() {
            return allowedDays;
        }

        public void setAllowedDays(List<Integer> allowedDays) {
            this.allowedDays = allowedDays;
        }
    }

    public static class Polling {

        private int processMetricsSeconds = 5;
        private int networkConnectionsSeconds = 3;
        private int lanScanSeconds = 60;
        private int systemHealthSeconds = 10;

        public int getProcessMetricsSeconds() {
            return processMetricsSeconds;
        }

        public void setProcessMetricsSeconds(int processMetricsSeconds) {
            this.processMetricsSeconds = processMetricsSeconds;
        }

        public int getNetworkConnectionsSeconds() {
            return networkConnectionsSeconds;
        }

        public void setNetworkConnectionsSeconds(int networkConnectionsSeconds) {
            this.networkConnectionsSeconds = networkConnectionsSeconds;
        }

        public int getLanScanSeconds() {
            return lanScanSeconds;
        }

        public void setLanScanSeconds(int lanScanSeconds) {
            this.lanScanSeconds = lanScanSeconds;
        }

        public int getSystemHealthSeconds() {
            return systemHealthSeconds;
        }

        public void setSystemHealthSeconds(int systemHealthSeconds) {
            this.systemHealthSeconds = systemHealthSeconds;
        }
    }

    public static class KnowledgeBase {

        private String basePath = "../knowledge-base";

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    }

    public static class Network {

        private boolean resolveDns = true;
        private int dnsTimeoutMs = 1000;

        public boolean isResolveDns() {
            return resolveDns;
        }

        public void setResolveDns(boolean resolveDns) {
            this.resolveDns = resolveDns;
        }

        public int getDnsTimeoutMs() {
            return dnsTimeoutMs;
        }

        public void setDnsTimeoutMs(int dnsTimeoutMs) {
            this.dnsTimeoutMs = dnsTimeoutMs;
        }
    }

    public static class Collector {

        private boolean autoStart = true;

        public boolean isAutoStart() {
            return autoStart;
        }

        public void setAutoStart(boolean autoStart) {
            this.autoStart = autoStart;
        }
    }

    private final Collector collector = new Collector();

    public Collector getCollector() {
        return collector;
    }
}
