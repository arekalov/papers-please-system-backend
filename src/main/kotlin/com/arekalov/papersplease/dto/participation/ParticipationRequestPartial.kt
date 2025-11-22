package com.arekalov.papersplease.dto.participation

import com.arekalov.papersplease.model.enums.Specialization
import jakarta.validation.constraints.PositiveOrZero

data class ParticipationRequestPartial(
    val shiftId: String? = null,
    val userId: String? = null,

    @field:PositiveOrZero(message = "Bonus coefficient must be positive or zero")
    val coeffBonus: Float? = null,

    @field:PositiveOrZero(message = "Penalty coefficient must be positive or zero")
    val coeffPenalty: Float? = null,

    val specialization: Specialization? = null,
)
