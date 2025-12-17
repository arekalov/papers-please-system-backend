package com.arekalov.papersplease.dto.event

import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.Specialization
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

data class EventRequest(
    val time: Instant = Instant.now(),

    @field:NotBlank(message = "Description is required")
    @field:Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    val description: String,

    val specialization: Specialization? = null,

    val priority: Priority = Priority.NORMAL,
)
