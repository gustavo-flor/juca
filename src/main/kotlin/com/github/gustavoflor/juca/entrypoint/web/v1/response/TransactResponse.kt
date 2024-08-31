package com.github.gustavoflor.juca.entrypoint.web.v1.response

import com.github.gustavoflor.juca.core.TransactionResult

data class TransactResponse(
    val code: String
) {
    companion object {
        fun error() = TransactResponse(TransactionResult.ERROR.code)
    }
}
