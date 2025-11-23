package com.arekalov.papersplease.model.entity

import com.arekalov.papersplease.model.enums.NotificationType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "notifications")
class Notification(
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    var notificationType: NotificationType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    var message: String,

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    var shift: Shift? = null,
) : BaseEntity()
