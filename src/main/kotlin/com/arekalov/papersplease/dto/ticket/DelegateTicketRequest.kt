package com.arekalov.papersplease.dto.ticket

import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.Specialization
import com.arekalov.papersplease.model.enums.TicketType
import jakarta.validation.constraints.NotNull

data class DelegateTicketRequest(
    @field:NotNull(message = "Ticket type is required")
    val ticketType: TicketType?,

    val specialization: Specialization? = null,

    val description: String? = null,

    val priority: Priority? = null,

    val subjectId: String? = null,

    val shiftId: String? = null,
)
