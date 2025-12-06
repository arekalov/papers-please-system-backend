package com.arekalov.papersplease.dto.participation

import com.arekalov.papersplease.model.enums.Specialization

data class ParticipationResponse(
    val id: String,
    val userId: String,
    val shiftId: String,
    val wage: Float,
    val penalty: Float,
    val specialization: Specialization,
    val totalResolvedTickets: Long,
)
