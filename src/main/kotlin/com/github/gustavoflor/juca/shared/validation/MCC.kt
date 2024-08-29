package com.github.gustavoflor.juca.shared.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [MCCConstraint::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class MCC(
    val message: String = "must be a valid merchant category code",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
