package com.example.resourceallocation.resourcecatalog.databank;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/databank")
public class DataBankController {

    private final DataBankService service;

    public DataBankController(DataBankService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DataBankEntry>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<DataBankEntry> getByKey(@PathVariable String key) {
        return service.findByKey(key)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DataBankEntry> upsert(@RequestBody DataBankEntry entry) {
        if (entry.getKey() == null || entry.getPayload() == null) {
            return ResponseEntity.badRequest().build();
        }
        DataBankEntry saved = service.upsert(entry.getKey(), entry.getPayload());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        service.deleteByKey(key);
        return ResponseEntity.noContent().build();
    }
}
