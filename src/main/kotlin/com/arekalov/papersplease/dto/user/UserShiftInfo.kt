package com.arekalov.papersplease.dto.user

import com.arekalov.papersplease.dto.upk.UpkResponse
import java.time.Instant

data class UserShiftInfo(
    val id: String,
    val startTime: Instant,
    val endTime: Instant?,
    val createdBy: String,
    val upkId: String,
    val boss: UserResponse,
    val upk: UpkResponse,
    val participation: UserParticipationInfo,
)
