package com.arekalov.papersplease.model.enums

enum class AppealDecision(val value: String) {
    APPROVED("approved"),
    REJECTED("rejected"),
    PUNISH_EXECUTOR("punish_executor"),
    PUNISH_CHECKER("punish_checker"),
    PUNISH_BOTH("punish_both"),
}
