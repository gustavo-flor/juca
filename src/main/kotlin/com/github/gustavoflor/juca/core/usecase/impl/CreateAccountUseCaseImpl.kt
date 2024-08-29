package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.repository.AccountRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.CreateAccountUseCase
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateAccountUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val walletRepository: WalletRepository
) : CreateAccountUseCase {
    @Transactional
    override fun execute(): CreateAccountUseCase.Output {
        val account = accountRepository.create()
        val wallets = MerchantCategory.entries.map { Wallet.of(account, it) }
            .let { walletRepository.createAll(it) }
        return CreateAccountUseCase.Output(account, wallets)
    }
}