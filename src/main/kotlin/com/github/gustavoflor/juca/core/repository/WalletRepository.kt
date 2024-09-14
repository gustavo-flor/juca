package com.github.gustavoflor.juca.core.repository

import com.github.gustavoflor.juca.core.entity.Wallet

interface WalletRepository {
    fun create(wallet: Wallet): Wallet

    fun findByAccountId(accountId: Long): Wallet?

    fun findByAccountIdForUpdate(accountId: Long): Wallet?

    fun update(wallet: Wallet): Wallet
}
