package com.arekalov.papersplease.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "events")
class Event(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    var shift: Shift,

    @Column(name = "time", nullable = false)
    var time: Instant,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String,
) : BaseEntity()
