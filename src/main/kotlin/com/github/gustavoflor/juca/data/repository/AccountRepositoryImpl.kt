package com.github.gustavoflor.juca.data.repository

import com.github.gustavoflor.juca.core.entity.Account
import com.github.gustavoflor.juca.core.repository.AccountRepository
import com.github.gustavoflor.juca.data.repository.command.CreateAccountCommand
import org.springframework.stereotype.Repository

@Repository
class AccountRepositoryImpl(
    private val createAccountCommand: CreateAccountCommand
) : AccountRepository {
    override fun create(): Account {
        return createAccountCommand.execute()
    }
}
