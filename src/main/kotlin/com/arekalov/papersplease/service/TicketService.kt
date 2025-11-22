package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.ticket.TicketRequest
import com.arekalov.papersplease.dto.ticket.TicketRequestPartial
import com.arekalov.papersplease.dto.ticket.TicketResponse
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.TicketStatus
import com.arekalov.papersplease.model.enums.TicketType
import com.arekalov.papersplease.repository.ShiftRepository
import com.arekalov.papersplease.repository.TicketRepository
import com.arekalov.papersplease.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Suppress("TooManyFunctions")
@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val userRepository: UserRepository,
    private val shiftRepository: ShiftRepository,
) {

    @Transactional(readOnly = true)
    suspend fun getAll(limit: Int, offset: Int): PagedResponse<TicketResponse> = withContext(Dispatchers.IO) {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = ticketRepository.findAll(pageable)

        PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    suspend fun getById(id: String): TicketResponse = withContext(Dispatchers.IO) {
        val ticket = ticketRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Ticket with id $id not found") }
        ticket.toResponse()
    }

    @Transactional(readOnly = true)
    suspend fun getByAuthor(authorId: String, limit: Int, offset: Int): PagedResponse<TicketResponse> =
        withContext(Dispatchers.IO) {
            val pageable = PageRequest.of(offset / limit, limit)
            val page = ticketRepository.findByAuthor_Id(UUID.fromString(authorId), pageable)

            PagedResponse(
                items = page.content.map { it.toResponse() },
                total = page.totalElements,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional(readOnly = true)
    suspend fun getByExecutor(executorId: String, limit: Int, offset: Int): PagedResponse<TicketResponse> =
        withContext(Dispatchers.IO) {
            val pageable = PageRequest.of(offset / limit, limit)
            val page = ticketRepository.findByExecutor_Id(UUID.fromString(executorId), pageable)

            PagedResponse(
                items = page.content.map { it.toResponse() },
                total = page.totalElements,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional(readOnly = true)
    suspend fun getByStatus(status: TicketStatus, limit: Int, offset: Int): PagedResponse<TicketResponse> =
        withContext(Dispatchers.IO) {
            val pageable = PageRequest.of(offset / limit, limit)
            val page = ticketRepository.findByStatus(status, pageable)

            PagedResponse(
                items = page.content.map { it.toResponse() },
                total = page.totalElements,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional(readOnly = true)
    suspend fun getByType(type: TicketType, limit: Int, offset: Int): PagedResponse<TicketResponse> =
        withContext(Dispatchers.IO) {
            val pageable = PageRequest.of(offset / limit, limit)
            val page = ticketRepository.findByTicketType(type, pageable)

            PagedResponse(
                items = page.content.map { it.toResponse() },
                total = page.totalElements,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional(readOnly = true)
    suspend fun getByPriority(priority: Priority, limit: Int, offset: Int): PagedResponse<TicketResponse> =
        withContext(Dispatchers.IO) {
            val pageable = PageRequest.of(offset / limit, limit)
            val page = ticketRepository.findByPriority(priority, pageable)

            PagedResponse(
                items = page.content.map { it.toResponse() },
                total = page.totalElements,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional(readOnly = true)
    suspend fun getByShift(shiftId: String, limit: Int, offset: Int): PagedResponse<TicketResponse> =
        withContext(Dispatchers.IO) {
            val pageable = PageRequest.of(offset / limit, limit)
            val page = ticketRepository.findByShift_Id(UUID.fromString(shiftId), pageable)

            PagedResponse(
                items = page.content.map { it.toResponse() },
                total = page.totalElements,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional
    suspend fun create(request: TicketRequest): TicketResponse = withContext(Dispatchers.IO) {
        val author = userRepository.findById(UUID.fromString(request.authorId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.authorId} not found") }

        val executor = request.executorId?.let {
            userRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("User with id $it not found") }
        }

        val shift = request.parentTicketId?.let {
            shiftRepository.findById(UUID.fromString(it))
                .orElse(null)
        }

        val ticket = request.toEntity(author, executor, shift)

        ticketRepository.save(ticket).toResponse()
    }

    @Transactional
    suspend fun update(id: String, request: TicketRequest): TicketResponse = withContext(Dispatchers.IO) {
        val ticket = ticketRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Ticket with id $id not found") }

        val author = userRepository.findById(UUID.fromString(request.authorId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.authorId} not found") }

        val executor = request.executorId?.let {
            userRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("User with id $it not found") }
        }

        ticket.apply {
            ticketType = request.ticketType
            status = request.status
            priority = request.priority
            deadlineAt = request.deadlineAt
            this.author = author
            this.executor = executor
            updatedAt = Instant.now()
        }

        ticketRepository.save(ticket).toResponse()
    }

    @Transactional
    suspend fun partialUpdate(id: String, request: TicketRequestPartial): TicketResponse =
        withContext(Dispatchers.IO) {
            val ticket = ticketRepository.findById(UUID.fromString(id))
                .orElseThrow { ResourceNotFoundException("Ticket with id $id not found") }

            request.ticketType?.let { ticket.ticketType = it }
            request.status?.let { ticket.status = it }
            request.priority?.let { ticket.priority = it }
            request.deadlineAt?.let { ticket.deadlineAt = it }
            request.authorId?.let { authorId ->
                ticket.author = userRepository.findById(UUID.fromString(authorId))
                    .orElseThrow { ResourceNotFoundException("User with id $authorId not found") }
            }
            request.executorId?.let { executorId ->
                ticket.executor = userRepository.findById(UUID.fromString(executorId))
                    .orElseThrow { ResourceNotFoundException("User with id $executorId not found") }
            }

            ticket.updatedAt = Instant.now()

            ticketRepository.save(ticket).toResponse()
        }

    @Transactional
    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val ticket = ticketRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Ticket with id $id not found") }
        ticketRepository.delete(ticket)
    }

    @Transactional
    suspend fun assignExecutor(ticketId: String, executorId: String): TicketResponse = withContext(Dispatchers.IO) {
        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        val executor = userRepository.findById(UUID.fromString(executorId))
            .orElseThrow { ResourceNotFoundException("User with id $executorId not found") }

        ticket.executor = executor
        ticket.status = TicketStatus.IN_PROGRESS
        ticket.updatedAt = Instant.now()

        ticketRepository.save(ticket).toResponse()
    }

    @Transactional
    suspend fun closeTicket(ticketId: String, resolution: String): TicketResponse = withContext(Dispatchers.IO) {
        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        ticket.status = TicketStatus.CLOSED
        ticket.resolution = resolution
        ticket.updatedAt = Instant.now()

        ticketRepository.save(ticket).toResponse()
    }

    @Transactional
    suspend fun reopenTicket(ticketId: String): TicketResponse = withContext(Dispatchers.IO) {
        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        ticket.status = TicketStatus.OPEN
        ticket.updatedAt = Instant.now()

        ticketRepository.save(ticket).toResponse()
    }
}
