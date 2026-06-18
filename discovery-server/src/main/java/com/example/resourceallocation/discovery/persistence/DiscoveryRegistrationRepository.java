package com.example.resourceallocation.discovery.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscoveryRegistrationRepository extends JpaRepository<DiscoveryRegistration, Long> {
    Optional<DiscoveryRegistration> findByAppNameAndInstanceId(String appName, String instanceId);
}
