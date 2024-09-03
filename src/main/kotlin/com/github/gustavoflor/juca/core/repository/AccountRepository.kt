package com.github.gustavoflor.juca.core.repository

import com.github.gustavoflor.juca.core.entity.Account

interface AccountRepository {
    fun create(): Account

    fun findAll(): List<Account>
}
