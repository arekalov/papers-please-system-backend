package com.arekalov.papersplease.dto.event

import com.arekalov.papersplease.model.enums.Specialization
import java.time.Instant

data class EventResponse(
    val id: String,
    val shiftId: String,
    val time: Instant,
    val description: String,
    val specialization: Specialization? = null,
)
