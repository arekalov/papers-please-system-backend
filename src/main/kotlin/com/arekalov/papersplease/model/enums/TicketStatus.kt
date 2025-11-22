package com.arekalov.papersplease.model.enums

enum class TicketStatus(val value: String) {
    OPEN("open"),
    IN_PROGRESS("in_progress"),
    NEED_INFO("need_info"),
    CLOSED("closed"),
    REJECTED("rejected"),
}
