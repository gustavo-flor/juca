package com.github.gustavoflor.juca.core.policy.impl

import com.github.gustavoflor.juca.core.mapping.Policy
import com.github.gustavoflor.juca.core.policy.DebitPolicy
import java.math.BigDecimal

@Policy
class DebitPolicyImpl : DebitPolicy {
    override fun execute(input: DebitPolicy.Input): DebitPolicy.Output {
        val amount = input.amount
        val targetBalance = input.targetWallet.balance
        val fallbackBalance = input.fallbackWallet?.balance ?: BigDecimal.ZERO
        val totalBalance = targetBalance.add(fallbackBalance)
        if (totalBalance < amount) {
            return DebitPolicy.Output.insufficientBalance()
        }
        return DebitPolicy.Output.approved(
            targetAmount = amount.min(targetBalance),
            fallbackAmount = amount.subtract(targetBalance)
        )
    }
}
