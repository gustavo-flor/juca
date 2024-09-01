package com.github.gustavoflor.juca.shared.util

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.TransactionType
import com.github.gustavoflor.juca.core.entity.Account
import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.usecase.AddCreditUseCase
import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.request.CreditRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.request.TransactRequest
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

object Faker {
    fun id() = Random.nextLong(1, 99999)

    fun numerify(value: String): String {
        val builder = StringBuilder()
        for (index in value.indices) {
            if (value[index] == '#') {
                builder.append(Random.nextInt(10))
            } else {
                builder.append(value[index])
            }
        }
        return builder.toString()
    }

    fun merchantCategory() = MerchantCategory.entries.random()

    fun account() = Account(
        id = id(),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    fun transaction() = newTransaction().copy(
        id = id(),
        createdAt = LocalDateTime.now()
    )

    fun newTransaction() = Transaction(
        accountId = id(),
        externalId = UUID.randomUUID(),
        origin = numerify("Origin [###]"),
        type = TransactionType.entries.random(),
        amount = money(),
        result = TransactionResult.entries.random(),
        merchantCategory = merchantCategory()
    )

    fun wallet(merchantCategory: MerchantCategory = merchantCategory()) = Wallet(
        id = id(),
        accountId = id(),
        balance = money(),
        merchantCategory = merchantCategory,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    fun wallets() = MerchantCategory.entries.map { wallet(it) }

    fun money(
        from: Double = 0.0,
        until: Double = 9_999_999_999_999.99
    ): BigDecimal = Random.nextDouble(from, until).toBigDecimal().setScale(2, RoundingMode.HALF_UP)

    fun transactRequest(merchantCategory: MerchantCategory = merchantCategory()) = TransactRequest(
        accountId = id(),
        amount = money(),
        mcc = mcc(merchantCategory),
        merchant = numerify("PADARIA DO ZE*##            SAO PAULO BR"),
        externalId = UUID.randomUUID()
    )

    fun mcc(
        merchantCategory: MerchantCategory = merchantCategory()
    ) = merchantCategory.codes.randomOrNull() ?: numerify("0###")

    fun creditRequest(wallet: Wallet = wallet()) = CreditRequest(
        accountId = wallet.accountId,
        amount = money(),
        merchantCategory = merchantCategory()
    )

    fun creditUseCaseInput() = AddCreditUseCase.Input(
        accountId = id(),
        merchantCategory = merchantCategory(),
        amount = money()
    )

    fun transactUseCaseInput(merchantCategory: MerchantCategory = merchantCategory()) = TransactUseCase.Input(
        accountId = id(),
        amount = money(),
        mcc = mcc(merchantCategory),
        externalId = UUID.randomUUID(),
        merchantName = numerify("PAG*##"),
        address = "RIO DE JANEI BR"
    )
}
