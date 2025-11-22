package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Shift
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface ShiftRepository : JpaRepository<Shift, UUID> {
    fun findByUpk_Id(upkId: UUID): List<Shift>

    fun findByShiftDateAndUpk_Id(shiftDate: Instant, upkId: UUID): Shift?

    fun findByCreatedBy_Id(createdById: UUID): List<Shift>
}
