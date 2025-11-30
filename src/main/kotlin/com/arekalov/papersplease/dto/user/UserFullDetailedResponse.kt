package com.arekalov.papersplease.dto.user

import com.arekalov.papersplease.dto.upk.UpkResponse
import com.arekalov.papersplease.model.enums.Role

data class UserFullDetailedResponse(
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String? = null,
    val role: Role,
    val upk: UpkResponse? = null,
    val boss: UserResponse? = null,
    val shifts: List<UserShiftInfo> = emptyList(),
)
