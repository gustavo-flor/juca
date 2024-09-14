package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.entity.Wallet

interface FindWalletsByAccountIdUseCase {
    fun execute(input: Input): Output

    data class Input(
        val accountId: Long
    )

    data class Output(
        val wallet: Wallet
    )
}
