package com.jtracer.repository;

import com.jtracer.domain.entity.DomainIdentity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainIdentityRepository extends JpaRepository<DomainIdentity, String> {

    Optional<DomainIdentity> findByHostname(String hostname);
}
