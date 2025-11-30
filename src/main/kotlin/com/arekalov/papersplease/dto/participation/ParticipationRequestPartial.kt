package com.arekalov.papersplease.dto.participation

import com.arekalov.papersplease.model.enums.Specialization
import jakarta.validation.constraints.PositiveOrZero

data class ParticipationRequestPartial(
    val shiftId: String? = null,
    val userId: String? = null,

    @field:PositiveOrZero(message = "Wage must be positive or zero")
    val wage: Float? = null,

    @field:PositiveOrZero(message = "Penalty must be positive or zero")
    val penalty: Float? = null,

    val specialization: Specialization? = null,
)
