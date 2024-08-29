package com.github.gustavoflor.juca.core

enum class MerchantCategory(
    val codes: Array<out String> = arrayOf(),
) {
    FOOD(arrayOf("5411", "5412")),
    MEAL(arrayOf("5811", "5812")),
    CASH;

    companion object {
        fun getByCode(code: String) = MerchantCategory.entries.firstOrNull() { it.codes.contains(code) } ?: CASH
    }
}
