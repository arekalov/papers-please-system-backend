package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Shift
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface ShiftRepository : JpaRepository<Shift, UUID> {
    fun findByUpk_Id(upkId: UUID): List<Shift>

    fun findByShiftDateAndUpk_Id(shiftDate: Instant, upkId: UUID): Shift?

    fun findByCreatedBy_Id(createdById: UUID): List<Shift>

    fun findByCreatedBy_Id(createdById: UUID, pageable: Pageable): Page<Shift>

    fun findByParticipations_User_Id(userId: UUID, pageable: Pageable): Page<Shift>
}
