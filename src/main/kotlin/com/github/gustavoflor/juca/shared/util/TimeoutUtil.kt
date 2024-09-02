package com.github.gustavoflor.juca.shared.util

import io.github.resilience4j.timelimiter.TimeLimiter
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import java.time.Duration
import java.util.concurrent.Future

object TimeoutUtil {
    fun <T> runUntil(
        timeoutDuration: Long,
        supplierTask: () -> Future<T>
    ): T = cancelableTimeLimiter(timeoutDuration).decorateFutureSupplier { supplierTask.invoke() }.call()

    private fun cancelableTimeLimiter(timeoutDuration: Long): TimeLimiter = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofMillis(timeoutDuration))
        .cancelRunningFuture(true)
        .build()
        .let { TimeLimiter.of(it) }
}
