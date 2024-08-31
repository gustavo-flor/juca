package com.github.gustavoflor.juca.data.repository

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.data.repository.command.CreateWalletCommand
import com.github.gustavoflor.juca.data.repository.command.UpdateWalletCommand
import com.github.gustavoflor.juca.data.repository.query.FindWalletByAccountIdAndMerchantCategoryForUpdateQuery
import com.github.gustavoflor.juca.data.repository.query.FindWalletsByAccountIdQuery
import org.springframework.stereotype.Repository

@Repository
class WalletRepositoryImpl(
    private val findWalletsByAccountIdQuery: FindWalletsByAccountIdQuery,
    private val findWalletsByAccountIdAndMerchantCategoryForUpdateQuery: FindWalletByAccountIdAndMerchantCategoryForUpdateQuery,
    private val createWalletCommand: CreateWalletCommand,
    private val updateWalletCommand: UpdateWalletCommand
) : WalletRepository {
    override fun createAll(wallets: List<Wallet>): List<Wallet> {
        return createWalletCommand.executeAll(wallets)
    }

    override fun findByAccountIdAndMerchantCategoryForUpdate(accountId: Long, merchantCategory: MerchantCategory): Wallet? {
        return findWalletsByAccountIdAndMerchantCategoryForUpdateQuery.execute(accountId, merchantCategory)
    }

    override fun findByAccountId(accountId: Long): List<Wallet> {
        return findWalletsByAccountIdQuery.execute(accountId)
    }

    override fun update(wallet: Wallet): Wallet {
        return updateWalletCommand.execute(wallet)
    }
}
