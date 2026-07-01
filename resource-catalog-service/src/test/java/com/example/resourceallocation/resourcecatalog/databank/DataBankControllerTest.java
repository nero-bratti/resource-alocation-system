package com.example.resourceallocation.resourcecatalog.databank;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DataBankController.class)
public class DataBankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataBankService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getByKeyNotFound() throws Exception {
        when(service.findByKey("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/databank/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void upsertAndGet() throws Exception {
        DataBankEntry entry = new DataBankEntry("k1", "{\"a\":1}");
        when(service.upsert("k1", "{\"a\":1}")).thenReturn(entry);
        when(service.findByKey("k1")).thenReturn(Optional.of(entry));

        MediaType jsonMediaType = Objects.requireNonNull(MediaType.APPLICATION_JSON);

        mockMvc.perform(post("/api/databank")
                .contentType(jsonMediaType)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(entry))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(jsonMediaType));

        mockMvc.perform(get("/api/databank/k1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(jsonMediaType));
    }

    @Test
    void deleteOk() throws Exception {
        mockMvc.perform(delete("/api/databank/k1")).andExpect(status().isNoContent());
    }
}
