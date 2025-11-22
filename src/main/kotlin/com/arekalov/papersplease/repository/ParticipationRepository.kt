package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Participation
import com.arekalov.papersplease.model.enums.Specialization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ParticipationRepository : JpaRepository<Participation, UUID> {
    fun findByShift_Id(shiftId: UUID): List<Participation>

    fun findByUser_Id(userId: UUID): List<Participation>

    fun findByShift_IdAndUser_Id(shiftId: UUID, userId: UUID): Participation?

    fun findBySpecialization(specialization: Specialization): List<Participation>
}
