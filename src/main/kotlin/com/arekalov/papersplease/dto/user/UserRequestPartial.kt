package com.arekalov.papersplease.dto.user

import com.arekalov.papersplease.model.enums.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UserRequestPartial(
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String? = null,

    @field:Email(message = "Email must be valid")
    val email: String? = null,

    val role: Role? = null,

    val upkId: String? = null,
)
