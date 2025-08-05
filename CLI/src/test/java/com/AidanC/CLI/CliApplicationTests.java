package com.AidanC.CLI;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.shell.interactive.enabled=false",
    "spring.main.web-application-type=none"
})
class CliApplicationTests {

	@Test
	void contextLoads() {
	}

}
