package com.arekalov.papersplease.dto.event

import com.arekalov.papersplease.model.enums.Priority

data class EventResponse(
    val id: String,
    val shiftId: String,
    val description: String,
    val priority: Priority,
)
