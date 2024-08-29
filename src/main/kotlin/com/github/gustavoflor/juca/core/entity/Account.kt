package com.github.gustavoflor.juca.core.entity

import java.time.LocalDateTime

data class Account(
    val id: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
