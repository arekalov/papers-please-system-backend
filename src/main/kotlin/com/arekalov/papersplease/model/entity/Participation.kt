package com.arekalov.papersplease.model.entity

import com.arekalov.papersplease.model.enums.Specialization
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "participations",
    indexes = [
        Index(name = "idx_participation_shift_user", columnList = "shift_id, user_id", unique = true),
    ],
)
class Participation(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    var shift: Shift,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "specialization", nullable = false)
    var specialization: Specialization,

    @Column(name = "accepted", nullable = false)
    var accepted: Boolean = false,

    @Column(name = "wage", nullable = false)
    var wage: Float = 1.0f,

    @Column(name = "penalty", nullable = false)
    var penalty: Float = 0.0f,
) : BaseEntity()
