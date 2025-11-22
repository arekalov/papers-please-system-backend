package com.arekalov.papersplease.dto.auth

import com.arekalov.papersplease.dto.user.UserResponse

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse,
)
