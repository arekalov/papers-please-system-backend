package com.arekalov.papersplease.dto.user

import com.arekalov.papersplease.dto.shift.ShiftResponse
import com.arekalov.papersplease.dto.upk.UpkResponse
import com.arekalov.papersplease.model.enums.Role

data class UserDetailedResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val upk: UpkResponse? = null,
    val subordinates: List<UserResponse> = emptyList(),
    val shifts: List<ShiftResponse> = emptyList(),
)
