package com.arekalov.papersplease.dto.participation

import com.arekalov.papersplease.model.enums.Specialization
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class ParticipationRequest(
    @field:NotBlank(message = "User ID is required")
    val userId: String,

    @field:NotBlank(message = "Shift ID is required")
    val shiftId: String,

    @field:PositiveOrZero(message = "Bonus coefficient must be positive or zero")
    val coeffBonus: Float = 1.0f,

    @field:PositiveOrZero(message = "Penalty coefficient must be positive or zero")
    val coeffPenalty: Float = 0.0f,

    @field:NotNull(message = "Specialization is required")
    val specialization: Specialization,
)
