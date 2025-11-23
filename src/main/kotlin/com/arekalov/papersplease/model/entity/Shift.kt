package com.arekalov.papersplease.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "shifts")
class Shift(
    @Column(name = "shift_date", nullable = false)
    var shiftDate: Instant,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upk_id", nullable = false)
    var upk: Upk,
) : BaseEntity() {
    @OneToMany(mappedBy = "shift", fetch = FetchType.LAZY)
    val participations: MutableSet<Participation> = mutableSetOf()
}
