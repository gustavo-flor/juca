package com.github.gustavoflor.juca.container

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
}
