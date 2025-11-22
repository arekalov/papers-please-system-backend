package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EventRepository : JpaRepository<Event, UUID> {
    fun findByShift_Id(shiftId: UUID): List<Event>
}
