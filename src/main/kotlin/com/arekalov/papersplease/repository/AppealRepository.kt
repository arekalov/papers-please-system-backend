package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Appeal
import com.arekalov.papersplease.model.enums.AppealDecision
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AppealRepository : JpaRepository<Appeal, UUID> {
    fun findByTicket_Id(ticketId: UUID): Appeal?

    fun findByFiledBy_Id(filedById: UUID, pageable: Pageable): Page<Appeal>

    fun findByDecidedBy_Id(decidedById: UUID, pageable: Pageable): Page<Appeal>

    fun findByDecision(decision: AppealDecision, pageable: Pageable): Page<Appeal>
}
