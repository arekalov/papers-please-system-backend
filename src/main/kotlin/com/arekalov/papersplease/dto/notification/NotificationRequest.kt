package com.arekalov.papersplease.dto.notification

import com.arekalov.papersplease.model.enums.NotificationType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class NotificationRequest(
    @field:NotBlank(message = "User ID is required")
    val userId: String,

    val relatedTicketId: String? = null,

    @field:NotBlank(message = "Message is required")
    val message: String,

    @field:NotNull(message = "Notification type is required")
    val notificationType: NotificationType,
)
