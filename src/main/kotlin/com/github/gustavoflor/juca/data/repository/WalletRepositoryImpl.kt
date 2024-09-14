package com.github.gustavoflor.juca.data.repository

import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.data.repository.command.CreateWalletCommand
import com.github.gustavoflor.juca.data.repository.command.UpdateWalletCommand
import com.github.gustavoflor.juca.data.repository.query.FindWalletByAccountIdForUpdateQuery
import com.github.gustavoflor.juca.data.repository.query.FindWalletByAccountIdQuery
import org.springframework.stereotype.Repository

@Repository
class WalletRepositoryImpl(
    private val findWalletByAccountIdQuery: FindWalletByAccountIdQuery,
    private val findWalletByAccountIdForUpdateQuery: FindWalletByAccountIdForUpdateQuery,
    private val createWalletCommand: CreateWalletCommand,
    private val updateWalletCommand: UpdateWalletCommand
) : WalletRepository {
    override fun create(wallet: Wallet): Wallet {
        return createWalletCommand.execute(wallet)
    }

    override fun findByAccountIdForUpdate(accountId: Long): Wallet? {
        return findWalletByAccountIdForUpdateQuery.execute(accountId)
    }

    override fun findByAccountId(accountId: Long): Wallet? {
        return findWalletByAccountIdQuery.execute(accountId)
    }

    override fun update(wallet: Wallet): Wallet {
        return updateWalletCommand.execute(wallet)
    }
}
