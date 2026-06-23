package com.jtracer.repository;

import com.jtracer.domain.entity.NetworkConnection;
import com.jtracer.domain.enums.ConnectionState;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NetworkConnectionRepository extends JpaRepository<NetworkConnection, String> {

    Optional<NetworkConnection> findBySession_IdAndProtocolAndLocalIpAndLocalPortAndRemoteIpAndRemotePort(
            String sessionId,
            com.jtracer.domain.enums.ProtocolType protocol,
            String localIp,
            Integer localPort,
            String remoteIp,
            Integer remotePort);

    List<NetworkConnection> findBySession_IdAndState(String sessionId, ConnectionState state);

    long countBySession_IdAndState(String sessionId, ConnectionState state);

    @Query("SELECT DISTINCT nc.remoteIp FROM NetworkConnection nc WHERE nc.remoteIp IS NOT NULL")
    List<String> findDistinctRemoteIps();
}
