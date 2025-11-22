package com.arekalov.papersplease.dto.user

import com.arekalov.papersplease.model.enums.Role

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val upkId: String? = null,
)
