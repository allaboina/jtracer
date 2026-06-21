package com.jtracer.repository;

import com.jtracer.domain.entity.RemoteEndpoint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemoteEndpointRepository extends JpaRepository<RemoteEndpoint, String> {

    Optional<RemoteEndpoint> findByIpAddressAndPort(String ipAddress, Integer port);
}
