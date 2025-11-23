package com.arekalov.papersplease.dto.event

import com.arekalov.papersplease.model.enums.Specialization
import jakarta.validation.constraints.Size
import java.time.Instant

data class EventRequestPartial(
    val shiftId: String? = null,

    val time: Instant? = null,

    @field:Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    val description: String? = null,

    val specialization: Specialization? = null,
)
