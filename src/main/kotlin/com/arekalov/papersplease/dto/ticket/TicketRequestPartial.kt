package com.arekalov.papersplease.dto.ticket

import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.TicketStatus
import com.arekalov.papersplease.model.enums.TicketType
import java.time.Instant

data class TicketRequestPartial(
    val ticketType: TicketType? = null,
    val status: TicketStatus? = null,
    val priority: Priority? = null,
    val deadlineAt: Instant? = null,
    val executorId: String? = null,
    val resolvedAt: Instant? = null,
)
