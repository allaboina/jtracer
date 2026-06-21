package com.jtracer.collector.common;

import com.jtracer.domain.enums.Confidence;
import java.util.Optional;

/**
 * Resolves IP addresses to domain hostnames using reverse DNS.
 */
public interface DomainResolver {

    Optional<ResolvedDomain> resolve(String ipAddress);

    record ResolvedDomain(String hostname, String rootDomain, Confidence confidence) {
    }
}
