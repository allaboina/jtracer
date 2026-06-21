package com.jtracer.repository;

import com.jtracer.domain.entity.ObservedProcess;
import com.jtracer.domain.enums.ProcessStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservedProcessRepository extends JpaRepository<ObservedProcess, String> {

    Optional<ObservedProcess> findBySession_IdAndPidAndStatus(
            String sessionId, Integer pid, ProcessStatus status);

    List<ObservedProcess> findBySession_IdAndStatus(String sessionId, ProcessStatus status);
}
