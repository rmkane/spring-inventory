package org.acme.inventory.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.acme.inventory.app.App;

/**
 * RestClient against {@code @SpringBootTest} with Testcontainers Postgres.
 */
@SpringBootTest(classes = {
        App.class,
        RestClientTestSuite.PostgresContainerConfig.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class RestClientTestSuite {

    private static final Path TEST_DIR = Paths.get("target/integration/modern");

    protected static ObjectMapper objectMapper;

    @LocalServerPort
    int port;

    protected RestClient restClient;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            Files.createDirectories(TEST_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUpRestClient() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeaders(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                })
                .build();
    }

    protected <T> ResponseEntity<T> get(String path, Class<T> type) {
        return restClient.get().uri(path).retrieve().toEntity(type);
    }

    protected <T> ResponseEntity<T> get(String path, ParameterizedTypeReference<T> type) {
        return restClient.get().uri(path).retrieve().toEntity(type);
    }

    protected <T> ResponseEntity<T> post(String path, Object body, Class<T> type) {
        return restClient.post().uri(path).body(body).retrieve().toEntity(type);
    }

    protected <T> ResponseEntity<T> put(String path, Object body, Class<T> type) {
        return restClient.put().uri(path).body(body).retrieve().toEntity(type);
    }

    protected ResponseEntity<Void> delete(String path) {
        return restClient.delete().uri(path).retrieve().toBodilessEntity();
    }

    protected void write(Object object, String filename) {
        try {
            Files.createDirectories(TEST_DIR);
            objectMapper.writeValue(TEST_DIR.resolve(filename).toFile(), object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class PostgresContainerConfig {

        @Bean
        @ServiceConnection
        PostgreSQLContainer<?> postgres() {
            return new PostgreSQLContainer<>("postgres:17-alpine");
        }
    }
}
