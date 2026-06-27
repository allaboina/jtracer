package com.jtracer.repository;

import com.jtracer.domain.entity.NetworkConnection;
import com.jtracer.domain.enums.ConnectionState;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<NetworkConnection> findBySession_IdAndStateOrderByLastSeenAtDesc(
            String sessionId, ConnectionState state);

    List<NetworkConnection> findBySession_IdAndStateAndProtocolOrderByLastSeenAtDesc(
            String sessionId, ConnectionState state, com.jtracer.domain.enums.ProtocolType protocol);

    List<NetworkConnection> findByObservedProcess_IdAndStateOrderByLastSeenAtDesc(
            String observedProcessId, ConnectionState state);

    List<NetworkConnection> findBySession_IdAndObservedProcess_IdAndStateOrderByLastSeenAtDesc(
            String sessionId, String observedProcessId, ConnectionState state);

    @Query("""
            SELECT nc.observedProcess.id, COUNT(nc)
            FROM NetworkConnection nc
            WHERE nc.session.id = :sessionId
              AND nc.state = com.jtracer.domain.enums.ConnectionState.ACTIVE
              AND nc.observedProcess IS NOT NULL
            GROUP BY nc.observedProcess.id
            """)
    List<Object[]> countActiveConnectionsGroupedByProcess(@Param("sessionId") String sessionId);
}
