package com.arekalov.papersplease.dto.ticket

import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.TicketStatus
import com.arekalov.papersplease.model.enums.TicketType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class TicketRequest(
    @field:NotNull(message = "Ticket type is required")
    val ticketType: TicketType,

    @field:NotNull(message = "Status is required")
    val status: TicketStatus,

    val priority: Priority = Priority.NORMAL,

    val deadlineAt: Instant? = null,

    @field:NotBlank(message = "Author ID is required")
    val authorId: String,

    @field:NotBlank(message = "Subject ID is required")
    val subjectId: String,

    val executorId: String? = null,

    val shiftId: String? = null,

    @field:NotBlank(message = "Description is required")
    val description: String,

    val resolution: String? = null,
)
