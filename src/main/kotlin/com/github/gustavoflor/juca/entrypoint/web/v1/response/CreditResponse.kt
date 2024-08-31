package com.github.gustavoflor.juca.entrypoint.web.v1.response

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

data class CreditResponse(
    @Schema(example = "100.0")
    val balance: BigDecimal
)
