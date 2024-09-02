package com.github.gustavoflor.juca.container

import org.testcontainers.containers.GenericContainer

class RedisContainer<SELF : RedisContainer<SELF>>(dockerImageName: String) : GenericContainer<SELF>(dockerImageName) {
    init {
        withExposedPorts(6379)
    }

    fun getPort(): Int = getMappedPort(6379)
}
