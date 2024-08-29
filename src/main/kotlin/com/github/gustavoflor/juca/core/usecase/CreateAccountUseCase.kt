package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.entity.Account
import com.github.gustavoflor.juca.core.entity.Wallet

interface CreateAccountUseCase {
    fun execute(): Output

    data class Output(
        val account: Account,
        val wallets: List<Wallet>
    )
}
