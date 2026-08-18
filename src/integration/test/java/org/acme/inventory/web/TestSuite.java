package org.acme.inventory.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class TestSuite {

    private static final Path TEST_DIR = Paths.get("target/integration");

    protected static RestTemplate restTemplate;
    protected static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .enable(SerializationFeature.INDENT_OUTPUT);

        try {
            Files.createDirectories(TEST_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
