package com.github.gustavoflor.juca.core.repository

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.entity.Wallet

interface WalletRepository {
    fun createAll(wallets: List<Wallet>): List<Wallet>

    fun findByAccountId(accountId: Long): List<Wallet>

    fun findByAccountIdAndMerchantCategory(accountId: Long, merchantCategory: MerchantCategory): Wallet?

    fun update(wallet: Wallet): Wallet
}
