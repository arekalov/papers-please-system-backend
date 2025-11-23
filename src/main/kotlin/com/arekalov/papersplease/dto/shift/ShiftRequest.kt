package com.arekalov.papersplease.dto.shift

import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class ShiftRequest(
    val startTime: Instant? = null,
    val endTime: Instant? = null,

    @field:NotBlank(message = "UPK ID is required")
    val upkId: String,
)
