package com.arekalov.papersplease.model.enums

enum class NotificationType(val value: String) {
    TICKET_ASSIGNED("ticket_assigned"),
    TICKET_UPDATED("ticket_updated"),
    SHIFT_STARTED("shift_started"),
    APPEAL_RESULT("appeal_result"),
    EVENT_UPDATE("event_update"),
}
