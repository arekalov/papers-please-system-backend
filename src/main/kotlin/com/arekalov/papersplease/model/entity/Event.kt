package com.arekalov.papersplease.model.entity

import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.Specialization
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "events")
class Event(
    @Column(name = "time", nullable = false)
    var time: Instant,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "specialization")
    var specialization: Specialization? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    var priority: Priority = Priority.NORMAL,
) : BaseEntity()
