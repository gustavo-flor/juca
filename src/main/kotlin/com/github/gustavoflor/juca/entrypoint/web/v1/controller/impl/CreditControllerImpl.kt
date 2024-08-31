package com.github.gustavoflor.juca.entrypoint.web.v1.controller.impl

import com.github.gustavoflor.juca.core.usecase.CreditUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.controller.CreditController
import com.github.gustavoflor.juca.entrypoint.web.v1.request.CreditRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.response.CreditResponse
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController
class CreditControllerImpl(
    private val creditUseCase: CreditUseCase
) : CreditController {
    @Transactional
    override fun create(request: CreditRequest): CreditResponse {
        val input = request.input()
        val output = creditUseCase.execute(input)
        return CreditResponse(output.wallet.balance)
    }
}
