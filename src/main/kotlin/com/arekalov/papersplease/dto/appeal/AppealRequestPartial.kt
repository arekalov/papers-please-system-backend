package com.arekalov.papersplease.dto.appeal

import com.arekalov.papersplease.model.enums.AppealDecision
import com.arekalov.papersplease.model.enums.TicketStatus
import java.time.Instant

data class AppealRequestPartial(
    val status: TicketStatus? = null,
    val verdict: AppealDecision? = null,
    val comment: String? = null,
    val checkedBy: String? = null,
    val checkedAt: Instant? = null,
)
