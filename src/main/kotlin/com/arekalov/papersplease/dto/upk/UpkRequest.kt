package com.arekalov.papersplease.dto.upk

import com.arekalov.papersplease.model.enums.Region
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpkRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @field:NotNull(message = "Region is required")
    val region: Region,
)
