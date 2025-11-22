package com.arekalov.papersplease.dto.shift

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class ShiftRequest(
    @field:NotNull(message = "Shift date is required")
    val shiftDate: Instant,

    @field:NotBlank(message = "Creator ID is required")
    val createdBy: String,

    @field:NotBlank(message = "UPK ID is required")
    val upkId: String,
)
