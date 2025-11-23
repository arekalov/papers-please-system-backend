package com.arekalov.papersplease.dto.upk

import com.arekalov.papersplease.model.enums.Region

data class UpkResponse(
    val id: String,
    val name: String,
    val region: Region,
)
