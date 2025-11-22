package com.arekalov.papersplease.dto.notification

import com.arekalov.papersplease.model.enums.NotificationType
import java.time.Instant

data class NotificationResponse(
    val id: String,
    val userId: String,
    val relatedTicketId: String? = null,
    val message: String,
    val notificationType: NotificationType,
    val sentAt: Instant,
    val isRead: Boolean = false,
)
