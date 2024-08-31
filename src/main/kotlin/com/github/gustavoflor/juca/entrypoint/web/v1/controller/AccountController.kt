package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.entrypoint.web.v1.response.AccountResponse
import com.github.gustavoflor.juca.entrypoint.web.v1.response.StatementResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus

@RequestMapping("/v1/accounts")
interface AccountController {
    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable id: Long): AccountResponse

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun create(): AccountResponse

    @GetMapping(path = ["/{id}/statement"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getStatement(@PathVariable id: Long): StatementResponse
}