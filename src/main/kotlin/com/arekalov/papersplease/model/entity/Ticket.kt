package com.arekalov.papersplease.model.entity

import com.arekalov.papersplease.model.enums.AppealDecision
import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.TicketStatus
import com.arekalov.papersplease.model.enums.TicketType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(
    name = "tickets",
    indexes = [
        Index(name = "idx_ticket_executor_status", columnList = "executor_id, status"),
        Index(name = "idx_ticket_author", columnList = "author_id"),
    ],
)
class Ticket(
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type", nullable = false)
    var ticketType: TicketType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: TicketStatus,

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    var priority: Priority,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(name = "deadline_at")
    var deadlineAt: Instant? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    var subject: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id")
    var executor: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    var shift: Shift? = null,

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(name = "resolution", columnDefinition = "TEXT")
    var resolution: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "appeal_decision")
    var appealDecision: AppealDecision? = null,
) : BaseEntity() {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ticket_relations",
        joinColumns = [JoinColumn(name = "ticket_id")],
        inverseJoinColumns = [JoinColumn(name = "related_ticket_id")],
    )
    val relatedTickets: MutableSet<Ticket> = mutableSetOf()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ticket_documents",
        joinColumns = [JoinColumn(name = "ticket_id")],
        inverseJoinColumns = [JoinColumn(name = "document_id")],
        indexes = [
            Index(name = "idx_ticket_documents_ticket", columnList = "ticket_id"),
            Index(name = "idx_ticket_documents_document", columnList = "document_id"),
        ],
    )
    val documents: MutableSet<Document> = mutableSetOf()
}
