package com.arekalov.papersplease.dto.shift

data class ShiftFilterRequest(
    val createdBy: String? = null,
    val upkId: String? = null,
    val endTimeNotNull: Boolean? = null,
)
