package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Ticket
import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.TicketStatus
import com.arekalov.papersplease.model.enums.TicketType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TicketRepository : JpaRepository<Ticket, UUID> {
    fun findByAuthor_Id(authorId: UUID, pageable: Pageable): Page<Ticket>

    fun findByExecutor_Id(executorId: UUID, pageable: Pageable): Page<Ticket>

    fun findByStatus(status: TicketStatus, pageable: Pageable): Page<Ticket>

    fun findByTicketType(type: TicketType, pageable: Pageable): Page<Ticket>

    fun findByPriority(priority: Priority, pageable: Pageable): Page<Ticket>

    fun findByShift_Id(shiftId: UUID, pageable: Pageable): Page<Ticket>

    fun countByExecutor_IdAndStatusAndShift_Id(executorId: UUID, status: TicketStatus, shiftId: UUID): Long

    fun countByExecutor_IdAndStatusIn(executorId: UUID, statuses: List<TicketStatus>): Long
}
