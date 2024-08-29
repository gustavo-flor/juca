package com.github.gustavoflor.juca.shared.util

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.entity.Account
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.usecase.CreditUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.request.CreditRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.request.TransactRequest
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

object Faker {
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
        id = Random.nextLong(1, 99999),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    fun wallet(merchantCategory: MerchantCategory = merchantCategory()) = Wallet(
        id = Random.nextLong(1, 99999),
        accountId = Random.nextLong(1, 99999),
        balance = money(),
        merchantCategory = merchantCategory,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    fun wallets() = MerchantCategory.entries.map { wallet(it) }

    fun money(
        from: Double = 0.0,
        until: Double = 9999.9
    ) = Random.nextDouble(from, until).toBigDecimal().setScale(2, RoundingMode.HALF_UP)

    fun transactRequest(merchantCategory: MerchantCategory = merchantCategory()) = TransactRequest(
        accountId = Random.nextLong(1, 99999),
        amount = money(),
        mcc = merchantCategory.codes.randomOrNull() ?: Faker.numerify("#9##"),
        merchant = numerify("PADARIA DO ZE*##            SAO PAULO BR"),
        externalId = UUID.randomUUID().toString()
    )

    fun creditRequest(wallet: Wallet = wallet()) = CreditRequest(
        accountId = wallet.accountId,
        amount = money(),
        merchantCategory = merchantCategory()
    )

    fun creditUseCaseInput() = CreditUseCase.Input(
        accountId = Random.nextLong(1, 99999),
        merchantCategory = merchantCategory(),
        amount = money()
    )
}
