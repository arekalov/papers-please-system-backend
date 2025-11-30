package com.arekalov.papersplease.dto.user

import com.arekalov.papersplease.model.enums.Specialization

data class UserParticipationInfo(
    val userId: String,
    val shiftId: String,
    val wage: Float,
    val penalty: Float,
    val specialization: Specialization,
    val resolvedTickets: Int,
)
