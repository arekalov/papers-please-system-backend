package com.arekalov.papersplease.dto.shift

import com.arekalov.papersplease.dto.upk.UpkResponse
import com.arekalov.papersplease.dto.user.UserResponse
import java.time.Instant

data class ShiftDetailedResponse(
    val id: String,
    val startTime: Instant,
    val endTime: Instant?,
    val createdBy: String,
    val upk: UpkResponse,
    val boss: UserResponse,
    val inspectors: List<InspectorShiftInfo>,
)
