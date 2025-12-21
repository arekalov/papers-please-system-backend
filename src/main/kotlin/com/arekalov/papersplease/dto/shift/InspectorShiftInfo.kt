package com.arekalov.papersplease.dto.shift

import com.arekalov.papersplease.model.enums.Specialization

data class InspectorShiftInfo(
    val participationId: String,
    val userId: String,
    val name: String,
    val shiftId: String,
    val wage: Float,
    val penalty: Float,
    val specialization: Specialization,
    val resolvedTickets: Int,
    val passedCrossChecks: Int,
)
