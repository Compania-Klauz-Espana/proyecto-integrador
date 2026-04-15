package com.company.backend.item;

import com.company.backend.item.dto.CreateItemRequest;
import com.company.backend.shared.BaseIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ItemIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void crudFlow() throws Exception {
        // CREATE
        CreateItemRequest createReq = new CreateItemRequest("Integration Item", "Test desc", ItemStatus.ACTIVE);
        ResponseEntity<String> createResp = restTemplate.postForEntity("/api/items", createReq, String.class);
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        assertTrue(createResp.getBody().contains("Integration Item"));

        // Extract ID from create response
        JsonNode createBody = mapper.readTree(createResp.getBody());
        long createdId = createBody.get("id").asLong();

        // READ ALL
        ResponseEntity<String> listResp = restTemplate.getForEntity("/api/items", String.class);
        assertEquals(HttpStatus.OK, listResp.getStatusCode());
        assertTrue(listResp.getBody().contains("Integration Item"));

        // READ by ID
        ResponseEntity<String> getResp = restTemplate.getForEntity("/api/items/" + createdId, String.class);
        assertEquals(HttpStatus.OK, getResp.getStatusCode());

        // UPDATE
        String updateJson = "{\"name\":\"Updated Item\",\"status\":\"INACTIVE\"}";
        HttpEntity<String> updateEntity = new HttpEntity<>(updateJson, jsonHeaders());
        ResponseEntity<String> updateResp = restTemplate.exchange("/api/items/" + createdId, HttpMethod.PUT, updateEntity, String.class);
        assertEquals(HttpStatus.OK, updateResp.getStatusCode());
        assertTrue(updateResp.getBody().contains("Updated Item"));

        // DELETE
        restTemplate.delete("/api/items/" + createdId);
        ResponseEntity<String> afterDelete = restTemplate.getForEntity("/api/items/" + createdId, String.class);
        assertEquals(HttpStatus.NOT_FOUND, afterDelete.getStatusCode());
    }

    @Test
    void create_invalidRequest_returns400() {
        String invalidJson = "{\"name\":\"\",\"status\":null}";
        HttpEntity<String> entity = new HttpEntity<>(invalidJson, jsonHeaders());
        ResponseEntity<String> resp = restTemplate.postForEntity("/api/items", entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void findById_nonExisting_returns404() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/items/99999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    private org.springframework.http.HttpHeaders jsonHeaders() {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return headers;
    }
}
