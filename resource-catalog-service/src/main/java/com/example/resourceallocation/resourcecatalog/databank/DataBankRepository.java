package com.example.resourceallocation.resourcecatalog.databank;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DataBankRepository extends JpaRepository<DataBankEntry, Long> {
    Optional<DataBankEntry> findByKey(String key);
    void deleteByKey(String key);
}
