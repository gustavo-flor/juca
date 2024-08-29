package com.github.gustavoflor.juca.core

enum class TransactionResult(
    val code: String,
) {
    APPROVED("00"),
    INSUFFICIENT_BALANCE("51"),
    ERROR("07")
}