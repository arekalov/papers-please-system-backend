package com.arekalov.papersplease.dto.ticket

import com.arekalov.papersplease.model.enums.AppealDecision
import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.TicketStatus
import com.arekalov.papersplease.model.enums.TicketType
import java.time.Instant

data class TicketResponse(
    val id: String,
    val ticketType: TicketType,
    val status: TicketStatus,
    val priority: Priority,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deadlineAt: Instant? = null,
    val authorId: String,
    val subjectId: String,
    val executorId: String? = null,
    val shiftId: String? = null,
    val description: String,
    val resolution: String? = null,
    val appealDecision: AppealDecision? = null,
    val relatedTicketIds: List<String> = emptyList(),
    val documentIds: List<String> = emptyList(),
)
