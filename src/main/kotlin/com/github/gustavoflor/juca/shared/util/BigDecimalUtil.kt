package com.github.gustavoflor.juca.shared.util

import java.math.BigDecimal

object BigDecimalUtil {
    fun positiveOrNull(value: BigDecimal): BigDecimal? {
        return if (value > BigDecimal.ZERO) value else null
    }
}
