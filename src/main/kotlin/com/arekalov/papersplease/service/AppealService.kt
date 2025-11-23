package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.appeal.AppealRequest
import com.arekalov.papersplease.dto.appeal.AppealRequestPartial
import com.arekalov.papersplease.dto.appeal.AppealResponse
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.enums.AppealDecision
import com.arekalov.papersplease.repository.AppealRepository
import com.arekalov.papersplease.repository.TicketRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class AppealService(
    private val appealRepository: AppealRepository,
    private val ticketRepository: TicketRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(limit: Int, offset: Int): PagedResponse<AppealResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = appealRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: String): AppealResponse {
        val appeal = appealRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Appeal with id $id not found") }
        return appeal.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByTicket(ticketId: String): AppealResponse? {
        val appeal = appealRepository.findByTicket_Id(UUID.fromString(ticketId))
        return appeal?.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByFiledBy(userId: String, limit: Int, offset: Int): PagedResponse<AppealResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = appealRepository.findByCreatedBy_Id(UUID.fromString(userId), pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getByDecision(decision: AppealDecision, limit: Int, offset: Int): PagedResponse<AppealResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = appealRepository.findByDecision(decision, pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional
    fun create(request: AppealRequest): AppealResponse {
        val ticket = ticketRepository.findById(UUID.fromString(request.ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id ${request.ticketId} not found") }

        val createdBy = userRepository.findById(UUID.fromString(request.createdBy))
            .orElseThrow { ResourceNotFoundException("User with id ${request.createdBy} not found") }

        val appeal = request.toEntity(ticket, createdBy)

        return appealRepository.save(appeal).toResponse()
    }

    @Transactional
    fun update(id: String, request: AppealRequest): AppealResponse {
        val appeal = appealRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Appeal with id $id not found") }

        val ticket = ticketRepository.findById(UUID.fromString(request.ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id ${request.ticketId} not found") }

        val createdBy = userRepository.findById(UUID.fromString(request.createdBy))
            .orElseThrow { ResourceNotFoundException("User with id ${request.createdBy} not found") }

        appeal.apply {
            this.ticket = ticket
            this.createdBy = createdBy
            reason = request.comment.orEmpty()
        }

        return appealRepository.save(appeal).toResponse()
    }

    @Transactional
    fun partialUpdate(id: String, request: AppealRequestPartial): AppealResponse {
        val appeal = appealRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Appeal with id $id not found") }

        request.ticketId?.let { ticketId ->
            appeal.ticket = ticketRepository.findById(UUID.fromString(ticketId))
                .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }
        }
        request.createdBy?.let { createdById ->
            appeal.createdBy = userRepository.findById(UUID.fromString(createdById))
                .orElseThrow { ResourceNotFoundException("User with id $createdById not found") }
        }
        request.comment?.let { appeal.reason = it }

        return appealRepository.save(appeal).toResponse()
    }

    @Transactional
    fun delete(id: String) {
        val appeal = appealRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Appeal with id $id not found") }
        appealRepository.delete(appeal)
    }

    @Transactional
    fun processAppeal(
        appealId: String,
        decision: AppealDecision,
        decidedById: String,
        notes: String?,
    ): AppealResponse {
        val appeal = appealRepository.findById(UUID.fromString(appealId))
            .orElseThrow { ResourceNotFoundException("Appeal with id $appealId not found") }

        val decidedBy = userRepository.findById(UUID.fromString(decidedById))
            .orElseThrow { ResourceNotFoundException("User with id $decidedById not found") }

        appeal.apply {
            this.decision = decision
            this.decidedBy = decidedBy
            decidedAt = Instant.now()
            decisionNotes = notes
        }

        return appealRepository.save(appeal).toResponse()
    }
}
