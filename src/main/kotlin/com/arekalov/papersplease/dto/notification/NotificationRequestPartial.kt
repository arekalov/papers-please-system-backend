package com.arekalov.papersplease.dto.notification

import com.arekalov.papersplease.model.enums.NotificationType

data class NotificationRequestPartial(
    val userId: String? = null,
    val notificationType: NotificationType? = null,
    val message: String? = null,
    val isRead: Boolean? = null,
)
