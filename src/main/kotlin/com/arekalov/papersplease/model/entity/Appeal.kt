package com.arekalov.papersplease.model.entity

import com.arekalov.papersplease.model.enums.AppealDecision
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "appeals")
class Appeal(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    var ticket: Ticket,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    var reason: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "decision")
    var decision: AppealDecision? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    var decidedBy: User? = null,

    @Column(name = "decided_at")
    var decidedAt: Instant? = null,

    @Column(name = "decision_notes", columnDefinition = "TEXT")
    var decisionNotes: String? = null,
) : BaseEntity()
