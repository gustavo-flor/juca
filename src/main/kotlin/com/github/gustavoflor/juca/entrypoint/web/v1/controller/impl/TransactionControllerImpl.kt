package com.github.gustavoflor.juca.entrypoint.web.v1.controller.impl

import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.controller.TransactionController
import com.github.gustavoflor.juca.entrypoint.web.v1.request.TransactRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.response.TransactResponse
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionControllerImpl(
    private val transactUseCase: TransactUseCase
) : TransactionController {
    @Transactional
    override fun transact(request: TransactRequest): TransactResponse {
        val input = request.input()
        val output = transactUseCase.execute(input)
        return TransactResponse(output.result.code)
    }
}
