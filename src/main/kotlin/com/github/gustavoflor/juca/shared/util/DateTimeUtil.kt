package com.github.gustavoflor.juca.shared.util

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

object DateTimeUtil {
    val DATE_TIME_ISO_8601_FORMATTER: DateTimeFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .parseLenient()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
        .toFormatter()
}
