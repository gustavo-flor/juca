package com.github.gustavoflor.juca.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class ExecutorServiceConfig {
    @Bean
    fun transactionExecutorService(): ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
}
