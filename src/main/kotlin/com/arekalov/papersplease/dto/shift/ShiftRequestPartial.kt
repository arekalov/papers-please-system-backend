package com.arekalov.papersplease.dto.shift

import java.time.Instant

data class ShiftRequestPartial(
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val upkId: String? = null,
)
