package com.github.gustavoflor.juca.entrypoint.web.v1.request

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.shared.validation.MCC
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class TransactRequest(
    @field:NotBlank
    val externalId: String? = null,
    @field:NotNull
    @field:Positive
    val accountId: Long? = null,
    @field:NotNull
    @field:Positive
    val amount: BigDecimal? = null,
    @field:NotBlank
    @field:Size(min = 4, max = 4)
    @field:MCC
    val mcc: String? = null,
    @field:NotBlank
    val merchant: String? = null
) {
    fun input() = TransactUseCase.Input(
        externalId = externalId!!,
        accountId = accountId!!,
        amount = amount!!,
        merchantCategory = MerchantCategory.getByCode(mcc!!),
        merchantName = merchant!!
    )
}
