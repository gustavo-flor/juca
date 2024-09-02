package com.github.gustavoflor.juca.entrypoint

import com.github.gustavoflor.juca.Application
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [Application::class]
)
@ExtendWith(SpringExtension::class)
abstract class EntrypointTest {

    @LocalServerPort
    private lateinit var serverPort: String

    @BeforeEach
    fun setUp() {
        RestAssured.baseURI = "http://localhost:$serverPort/api"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Test
    fun `Given a local server port, when start API test, then should not be blank`() {
        assertThat(serverPort).isNotBlank()
    }
}