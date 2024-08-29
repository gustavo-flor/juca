package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.entity.Wallet

interface FindAccountDetailsByIdUseCase {
    fun execute(input: Input): Output

    data class Input(
        val id: Long
    )

    data class Output(
        val wallets: List<Wallet>
    )
}
