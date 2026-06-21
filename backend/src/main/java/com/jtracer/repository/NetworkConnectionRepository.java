package com.jtracer.repository;

import com.jtracer.domain.entity.NetworkConnection;
import com.jtracer.domain.enums.ConnectionState;
import com.jtracer.domain.enums.ProtocolType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkConnectionRepository extends JpaRepository<NetworkConnection, String> {

    Optional<NetworkConnection> findBySession_IdAndProtocolAndLocalIpAndLocalPortAndRemoteIpAndRemotePort(
            String sessionId,
            ProtocolType protocol,
            String localIp,
            Integer localPort,
            String remoteIp,
            Integer remotePort);

    List<NetworkConnection> findBySession_IdAndState(String sessionId, ConnectionState state);
}
