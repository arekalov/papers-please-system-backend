package com.arekalov.papersplease.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

@Suppress("SwallowedException")
class UUIDValidator : ConstraintValidator<ValidUUID, String?> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        return try {
            java.util.UUID.fromString(value)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
