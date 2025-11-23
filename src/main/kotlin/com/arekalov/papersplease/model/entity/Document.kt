package com.arekalov.papersplease.model.entity

import com.arekalov.papersplease.model.enums.DocumentType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "documents")
class Document(
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    var documentType: DocumentType,

    @Column(name = "body", columnDefinition = "TEXT", nullable = false)
    var body: String,

    @Column(name = "issued_at", nullable = false)
    var issuedAt: Instant,

    @Column(name = "expires_at")
    var expiresAt: Instant? = null,

    @Column(name = "uploaded_at", nullable = false)
    var uploadedAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    var owner: User,
) : BaseEntity() {
    @ManyToMany(mappedBy = "documents", fetch = FetchType.LAZY)
    val tickets: MutableSet<Ticket> = mutableSetOf()
}
