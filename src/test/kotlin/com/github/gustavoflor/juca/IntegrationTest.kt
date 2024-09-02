package com.github.gustavoflor.juca

import com.github.gustavoflor.juca.container.ContainerConfig
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.CountDownLatch

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = [Application::class]
)
@ExtendWith(SpringExtension::class)
@Import(ContainerConfig::class)
abstract class IntegrationTest {
    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    fun createAccount(): Long {
        val keyHolder = GeneratedKeyHolder()
        val sql = "INSERT INTO account DEFAULT VALUES"
        val params = mapOf<String, Any?>()
        jdbcTemplate.update(sql, MapSqlParameterSource(params), keyHolder, arrayOf("id"))
        return keyHolder.keys?.get("id") as Long
    }

    protected fun doSyncAndConcurrently(threadCount: Int, operation: (index: Int) -> Unit) {
        val startLatch = CountDownLatch(1)
        val endLatch = CountDownLatch(threadCount)
        for (index in 0 until threadCount) {
            Thread {
                try {
                    startLatch.await()
                    operation(index)
                } catch (e: Exception) {
                    System.err.printf("Error while executing operation on index = [%s]: %s%n", index, e.message)
                } finally {
                    endLatch.countDown()
                }
            }.start()
        }
        startLatch.countDown()
        endLatch.await()
    }
}
