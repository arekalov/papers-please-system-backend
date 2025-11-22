package com.arekalov.papersplease.dto.appeal

import com.arekalov.papersplease.model.enums.AppealDecision
import com.arekalov.papersplease.model.enums.TicketStatus
import java.time.Instant

data class AppealResponse(
    val id: String,
    val ticketId: String,
    val createdBy: String,
    val status: TicketStatus,
    val verdict: AppealDecision,
    val comment: String? = null,
    val createdAt: Instant,
    val checkedBy: String? = null,
    val checkedAt: Instant? = null,
)
