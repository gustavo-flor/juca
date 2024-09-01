package com.github.gustavoflor.juca.data

import com.github.gustavoflor.juca.Application
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.PostgreSQLContainer

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = [Application::class]
)
@ExtendWith(SpringExtension::class)
abstract class DataTest {
    companion object {
        private const val DATABASE_NAME = "juca"
        private const val MIGRATIONS_HOST_PATH = "./src/main/resources/migrations"
        private const val INIT_DB_ENTRYPOINT_CONTAINER_PATH = "/docker-entrypoint-initdb.d"

        private val postgresContainer = PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName(DATABASE_NAME)
            .withFileSystemBind(MIGRATIONS_HOST_PATH, INIT_DB_ENTRYPOINT_CONTAINER_PATH, BindMode.READ_ONLY)

        @BeforeAll
        @JvmStatic
        fun startContainers() {
            postgresContainer.start()
        }

        @AfterAll
        @JvmStatic
        fun stopContainers() {
            postgresContainer.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerContainers(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
        }
    }

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    fun createAccount(): Long {
        val keyHolder = GeneratedKeyHolder()
        val sql = "INSERT INTO account DEFAULT VALUES"
        val params = mapOf<String, Any?>()
        jdbcTemplate.update(sql, MapSqlParameterSource(params), keyHolder, arrayOf("id"))
        return keyHolder.keys?.get("id") as Long
    }
}
