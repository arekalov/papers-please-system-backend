package com.arekalov.papersplease.dto.shift

import java.time.Instant

data class ShiftResponse(
    val id: String,
    val shiftDate: Instant,
    val createdBy: String,
    val upkId: String,
)
