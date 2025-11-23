package com.arekalov.papersplease.dto.upk

import com.arekalov.papersplease.model.enums.Region
import jakarta.validation.constraints.Size

data class UpkRequestPartial(
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String? = null,

    val region: Region? = null,
)
