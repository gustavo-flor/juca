package com.github.gustavoflor.juca.data.repository

import com.github.gustavoflor.juca.core.entity.Account
import com.github.gustavoflor.juca.core.repository.AccountRepository
import com.github.gustavoflor.juca.data.repository.command.CreateAccountCommand
import com.github.gustavoflor.juca.data.repository.query.FindAccountsQuery
import org.springframework.stereotype.Repository

@Repository
class AccountRepositoryImpl(
    private val createAccountCommand: CreateAccountCommand,
    private val findAccountsQuery: FindAccountsQuery
) : AccountRepository {
    override fun create(): Account {
        return createAccountCommand.execute()
    }

    override fun findAll(): List<Account> {
        return findAccountsQuery.execute()
    }
}
