package com.arekalov.papersplease.dto.event

import com.arekalov.papersplease.model.enums.Priority
import jakarta.validation.constraints.Size

data class EventRequestPartial(
    @field:Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    val description: String? = null,

    val priority: Priority? = null,
)
