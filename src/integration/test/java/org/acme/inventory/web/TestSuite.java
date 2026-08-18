package org.acme.inventory.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class TestSuite {

    protected static final String BASE_URL = "http://localhost:8080";

    private static final Path TEST_DIR = Paths.get("target/integration");

    protected static RestTemplate restTemplate;
    protected static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }
        });
        objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .enable(SerializationFeature.INDENT_OUTPUT);

        try {
            Files.createDirectories(TEST_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String url(String path) {
        return BASE_URL + path;
    }

    protected static HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    protected <T> ResponseEntity<T> get(String path, Class<T> type) {
        return restTemplate.exchange(url(path), HttpMethod.GET, new HttpEntity<>(jsonHeaders()), type);
    }

    protected <T> ResponseEntity<T> get(String path, ParameterizedTypeReference<T> type) {
        return restTemplate.exchange(url(path), HttpMethod.GET, new HttpEntity<>(jsonHeaders()), type);
    }

    protected <T> ResponseEntity<T> post(String path, Object body, Class<T> type) {
        return restTemplate.exchange(url(path), HttpMethod.POST, new HttpEntity<>(body, jsonHeaders()), type);
    }

    protected <T> ResponseEntity<T> put(String path, Object body, Class<T> type) {
        return restTemplate.exchange(url(path), HttpMethod.PUT, new HttpEntity<>(body, jsonHeaders()), type);
    }

    protected ResponseEntity<Void> delete(String path) {
        return restTemplate.exchange(url(path), HttpMethod.DELETE, new HttpEntity<>(jsonHeaders()), Void.class);
    }

    protected void write(Object object, String filename) {
        try {
            Files.createDirectories(TEST_DIR);
            objectMapper.writeValue(TEST_DIR.resolve(filename).toFile(), object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
