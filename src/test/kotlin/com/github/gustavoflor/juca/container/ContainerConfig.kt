package com.github.gustavoflor.juca.container

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.PostgreSQLContainer


@Configuration
class ContainerConfig {
    companion object {
        private const val DATABASE_NAME = "juca"
        private const val MIGRATIONS_HOST_PATH = "./src/main/resources/migrations"
        private const val DOCKER_ENTRYPOINT_INIT_DB_CONTAINER_PATH = "/docker-entrypoint-initdb.d"
    }

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName(DATABASE_NAME)
            .withFileSystemBind(MIGRATIONS_HOST_PATH, DOCKER_ENTRYPOINT_INIT_DB_CONTAINER_PATH, BindMode.READ_ONLY)
    }

    @Bean
    fun redisContainer(registry: DynamicPropertyRegistry): RedisContainer<*> {
        val container = RedisContainer("redis:7.2.4-alpine")
        registry.add("spring.data.redis.host") { container.host }
        registry.add("spring.data.redis.port") { container.getPort() }
        return container
    }
}
