package com.github.gustavoflor.juca.shared.util

import com.github.gustavoflor.juca.shared.util.DateTimeUtil.DATE_TIME_ISO_8601_FORMATTER
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month

class DateTimeUtilTest {
    @Test
    fun `Given a date, when parse, then should transform into local date time`() {
        val date = "2024-08-24 19:39:27"

        val parsedDate = LocalDateTime.parse(date, DATE_TIME_ISO_8601_FORMATTER)

        assertThat(parsedDate.year).isEqualTo(2024)
        assertThat(parsedDate.month).isEqualTo(Month.AUGUST)
        assertThat(parsedDate.dayOfMonth).isEqualTo(24)
        assertThat(parsedDate.hour).isEqualTo(19)
        assertThat(parsedDate.minute).isEqualTo(39)
        assertThat(parsedDate.second).isEqualTo(27)
    }
}
