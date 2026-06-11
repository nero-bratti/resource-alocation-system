package com.example.resourceallocation.resourcecatalog.databank;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataBankService {

    private final DataBankRepository repository;

    public DataBankService(DataBankRepository repository) {
        this.repository = repository;
    }

    public List<DataBankEntry> listAll() {
        return repository.findAll();
    }

    public Optional<DataBankEntry> findByKey(String key) {
        return repository.findByKey(key);
    }

    @Transactional
    public DataBankEntry upsert(String key, String payload) {
        return repository.findByKey(key)
                .map(e -> {
                    e.setPayload(payload);
                    return repository.save(e);
                })
                .orElseGet(() -> repository.save(new DataBankEntry(key, payload)));
    }

    @Transactional
    public void deleteByKey(String key) {
        repository.deleteByKey(key);
    }
}
