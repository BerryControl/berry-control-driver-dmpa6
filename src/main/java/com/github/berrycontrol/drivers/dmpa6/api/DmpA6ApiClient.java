package com.github.berrycontrol.drivers.dmpa6.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DmpA6ApiClient {
    public static final int PORT = 9529;

    ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final String address;

    public DmpA6ApiClient(String address) {
        this.address = address;
    }

    public Map<String,Object> connect(String clientName, UUID clientUuid) {
        try {
            ResponseEntity<String> response =
                restTemplate
                    .getForEntity(
                        String.format(
                            "http://%s:%d/ZidooControlCenter/connect?name=%s&uuid=%s&tag=0&type=1",
                            address,
                            PORT,
                            clientName,
                            clientUuid.toString()),
                        String.class);
            TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};

            return objectMapper.readValue(response.getBody(), typeRef);
        } catch (Exception ex) {
            throw new RuntimeException("Error while loading device info.", ex);
        }
    }
}
