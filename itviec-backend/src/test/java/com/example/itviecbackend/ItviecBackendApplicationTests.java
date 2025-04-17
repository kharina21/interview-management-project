package com.example.itviecbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class ItviecBackendApplicationTests {

    @Test
    void contextLoads() {
        // This test will pass if the Spring application context loads successfully
    }
}
