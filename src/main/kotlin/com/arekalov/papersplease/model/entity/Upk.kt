package com.arekalov.papersplease.model.entity

import com.arekalov.papersplease.model.enums.Region
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "upks")
class Upk(
    @Column(name = "name", nullable = false, unique = true)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boss_id", nullable = false, unique = true)
    var boss: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    var region: Region,
) : BaseEntity()
