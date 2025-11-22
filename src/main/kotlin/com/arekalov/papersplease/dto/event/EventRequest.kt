package com.arekalov.papersplease.dto.event

import com.arekalov.papersplease.model.enums.Priority
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class EventRequest(
    @field:NotBlank(message = "Shift ID is required")
    val shiftId: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    val description: String,

    val priority: Priority = Priority.NORMAL,
)
