package com.github.gustavoflor.juca.core.exception

class AccountNotFoundException(
    override val message: String = "Account not found"
) : RuntimeException(message)
