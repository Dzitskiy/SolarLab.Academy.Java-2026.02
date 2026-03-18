package com.solarl.education.controller;

import com.solarl.education.repository.ClientRepository;
import com.solarl.education.request.ClientRequest;
import com.solarl.education.response.ClientView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ClientControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("solarl_test")
                    .withUsername("user")
                    .withPassword("password");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL::getPassword);
    }

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void clearClients() {
        clientRepository.deleteAll();
    }

    @Test
    void shouldApplyFlywayMigrationsBeforeControllerScenario() {
        Integer appliedMigrations = jdbcTemplate.queryForObject(
                "select count(*) from flyway_schema_history where success = true and version in ('1', '2')",
                Integer.class
        );

        assertThat(appliedMigrations).isEqualTo(2);
    }

    @Test
    void shouldCreateAndGetClientUsingRealPostgres() {
        ClientRequest request = ClientRequest.builder()
                .name("Alice")
                .email("alice@example.com")
                .build();

        ResponseEntity<ClientView> createResponse =
                restTemplate.postForEntity("/v1/clients", request, ClientView.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getName()).isEqualTo("Alice");
        assertThat(createResponse.getBody().getEmail()).isEqualTo("alice@example.com");

        Long clientId = createResponse.getBody().getId();

        ResponseEntity<ClientView> getResponse =
                restTemplate.getForEntity("/v1/clients/{id}", ClientView.class, clientId);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(clientId);
        assertThat(getResponse.getBody().getName()).isEqualTo("Alice");
        assertThat(getResponse.getBody().getEmail()).isEqualTo("alice@example.com");
        assertThat(clientRepository.findById(clientId)).isPresent();
    }
}
