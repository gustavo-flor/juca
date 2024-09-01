package com.github.gustavoflor.juca.entrypoint.web.v1.request

import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.shared.validation.MCC
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

data class TransactRequest(
    @field:NotNull
    val externalId: UUID? = null,
    @field:NotNull
    @field:Positive
    val accountId: Long? = null,
    @field:NotNull
    @field:Positive
    @field:Digits(integer = 14, fraction = 2)
    @Schema(example = "100.0")
    val amount: BigDecimal? = null,
    @field:NotNull
    @field:MCC
    @Schema(example = "5411")
    val mcc: String? = null,
    @field:NotNull
    @field:Size(min = 40, max = 40, message = "must have exactly 40 chars")
    @Schema(example = "UBER EATS                   SAO PAULO BR")
    val merchant: String? = null
) {
    fun input(): TransactUseCase.Input {
        val merchantName = merchant!!.take(25).trim()
        val address = merchant.takeLast(15).trim()
        return TransactUseCase.Input(
            externalId = externalId!!,
            accountId = accountId!!,
            amount = amount!!,
            mcc = mcc!!,
            merchantName = merchantName,
            address = address
        )
    }
}
