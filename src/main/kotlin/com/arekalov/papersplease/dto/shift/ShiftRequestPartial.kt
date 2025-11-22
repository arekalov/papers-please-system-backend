package com.arekalov.papersplease.dto.shift

import java.time.Instant

data class ShiftRequestPartial(
    val shiftDate: Instant? = null,
    val createdBy: String? = null,
    val upkId: String? = null,
)
