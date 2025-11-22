package com.arekalov.papersplease.dto.appeal

import com.arekalov.papersplease.model.enums.TicketStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AppealRequest(
    @field:NotBlank(message = "Ticket ID is required")
    val ticketId: String,

    @field:NotBlank(message = "Created by ID is required")
    val createdBy: String,

    @field:NotNull(message = "Status is required")
    val status: TicketStatus,

    val comment: String? = null,
)
