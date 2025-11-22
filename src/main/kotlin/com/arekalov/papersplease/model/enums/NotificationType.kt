package com.arekalov.papersplease.model.enums

enum class NotificationType(val value: String) {
    TICKET_STATUS_CHANGE("ticket_status_change"),
    TICKET_ASSIGNED("ticket_assigned"),
    SHIFT_INVITATION("shift_invitation"),
    APPEAL_RESULT("appeal_result"),
    EVENT_UPDATE("event_update"),
}
