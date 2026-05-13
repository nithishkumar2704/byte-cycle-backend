package com.bytecycle.fooddonor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test to verify the Spring application context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class ByteCycleApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring application context starts without errors
    }
}
