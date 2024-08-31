package com.github.gustavoflor.juca.shared.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class MCCConstraint : ConstraintValidator<MCC, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }
        return value.length == 4 && value.all { it.isDigit() }
    }
}
