package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.document.DocumentResponse
import com.arekalov.papersplease.dto.ticket.TicketRequest
import com.arekalov.papersplease.dto.ticket.TicketRequestPartial
import com.arekalov.papersplease.dto.ticket.TicketResponse
import com.arekalov.papersplease.exception.ForbiddenException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Ticket
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.NotificationType
import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.model.enums.TicketStatus
import com.arekalov.papersplease.model.enums.TicketType
import com.arekalov.papersplease.repository.DocumentRepository
import com.arekalov.papersplease.repository.ShiftRepository
import com.arekalov.papersplease.repository.TicketRepository
import com.arekalov.papersplease.repository.UserRepository
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
    private val documentRepository: DocumentRepository,
    private val notificationService: NotificationService,
) {

    @Transactional(readOnly = true)
    fun getAll(currentUserId: String, limit: Int, offset: Int): PagedResponse<TicketResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = when (currentUser.role) {
            Role.GOD -> ticketRepository.findAll(pageable)
            Role.MIGRANT -> ticketRepository.findByAuthor_Id(currentUser.id!!, pageable)
            else -> ticketRepository.findAll(pageable)
        }

        return PagedResponse(
            items = page.content.filter { checkReadAccess(currentUser, it) }.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(currentUserId: String, id: String): TicketResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Ticket with id $id not found") }

        if (!checkReadAccess(currentUser, ticket)) {
            throw ForbiddenException("You don't have access to this ticket")
        }

        return ticket.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByAuthor(currentUserId: String, authorId: String, limit: Int, offset: Int): PagedResponse<TicketResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = ticketRepository.findByAuthor_Id(UUID.fromString(authorId), pageable)

        return PagedResponse(
            items = page.content.filter { checkReadAccess(currentUser, it) }.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getByExecutor(
        currentUserId: String,
        executorId: String,
        limit: Int,
        offset: Int,
    ): PagedResponse<TicketResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = ticketRepository.findByExecutor_Id(UUID.fromString(executorId), pageable)

        return PagedResponse(
            items = page.content.filter { checkReadAccess(currentUser, it) }.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getByStatus(
        currentUserId: String,
        status: TicketStatus,
        limit: Int,
        offset: Int,
    ): PagedResponse<TicketResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = ticketRepository.findByStatus(status, pageable)

        return PagedResponse(
            items = page.content.filter { checkReadAccess(currentUser, it) }.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getByType(currentUserId: String, type: TicketType, limit: Int, offset: Int): PagedResponse<TicketResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = ticketRepository.findByTicketType(type, pageable)

        return PagedResponse(
            items = page.content.filter { checkReadAccess(currentUser, it) }.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getByPriority(
        currentUserId: String,
        priority: Priority,
        limit: Int,
        offset: Int,
    ): PagedResponse<TicketResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = ticketRepository.findByPriority(priority, pageable)

        return PagedResponse(
            items = page.content.filter { checkReadAccess(currentUser, it) }.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getByShift(currentUserId: String, shiftId: String, limit: Int, offset: Int): PagedResponse<TicketResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = ticketRepository.findByShift_Id(UUID.fromString(shiftId), pageable)

        return PagedResponse(
            items = page.content.filter { checkReadAccess(currentUser, it) }.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional
    fun create(currentUserId: String, request: TicketRequest): TicketResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        if (currentUser.role == Role.MIGRANT &&
            request.ticketType != TicketType.EXTERNAL &&
            request.ticketType != TicketType.APPEAL
        ) {
            throw ForbiddenException("Migrants can only create EXTERNAL and APPEAL tickets")
        }

        if (request.ticketType == TicketType.APPEAL && currentUser.role != Role.MIGRANT &&
            currentUser.role != Role.GOD
        ) {
            throw ForbiddenException("Only migrants can create APPEAL tickets")
        }

        val author = userRepository.findById(UUID.fromString(request.authorId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.authorId} not found") }

        val subject = userRepository.findById(UUID.fromString(request.subjectId))
            .orElseThrow { ResourceNotFoundException("Subject user with id ${request.subjectId} not found") }

        val executor = request.executorId?.let {
            userRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("User with id $it not found") }
        }

        val shift = request.shiftId?.let {
            shiftRepository.findById(UUID.fromString(it))
                .orElse(null)
        }

        val ticket = request.toEntity(author, subject, executor, shift)

        val savedTicket = ticketRepository.save(ticket)

        executor?.let {
            notificationService.createSystemNotification(
                userId = it.id!!,
                type = NotificationType.TICKET_ASSIGNED,
                message = "You have been assigned to ticket #${savedTicket.id}: ${savedTicket.description}",
            )
        }

        return savedTicket.toResponse()
    }

    @Transactional
    fun partialUpdate(currentUserId: String, id: String, request: TicketRequestPartial): TicketResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Ticket with id $id not found") }

        checkUpdateAccess(currentUser, ticket)

        val oldExecutorId = ticket.executor?.id

        request.ticketType?.let { ticket.ticketType = it }
        request.status?.let { ticket.status = it }
        request.priority?.let { ticket.priority = it }
        request.deadlineAt?.let { ticket.deadlineAt = it }
        request.description?.let { ticket.description = it }
        request.resolution?.let { ticket.resolution = it }
        request.appealDecision?.let { ticket.appealDecision = it }
        request.executorId?.let { executorId ->
            ticket.executor = userRepository.findById(UUID.fromString(executorId))
                .orElseThrow { ResourceNotFoundException("User with id $executorId not found") }
        }
        request.shiftId?.let { shiftId ->
            ticket.shift = shiftRepository.findById(UUID.fromString(shiftId))
                .orElse(null)
        }

        ticket.updatedAt = Instant.now()

        val savedTicket = ticketRepository.save(ticket)

        if (ticket.executor != null && ticket.executor?.id != oldExecutorId) {
            notificationService.createSystemNotification(
                userId = ticket.executor!!.id!!,
                type = NotificationType.TICKET_ASSIGNED,
                message = "You have been assigned to ticket #${savedTicket.id}: ${savedTicket.description}",
            )
        } else if (ticket.executor != null && oldExecutorId == ticket.executor?.id) {
            notificationService.createSystemNotification(
                userId = ticket.executor!!.id!!,
                type = NotificationType.TICKET_UPDATED,
                message = "Your ticket #${savedTicket.id} has been updated",
            )
        }

        return savedTicket.toResponse()
    }

    @Transactional
    fun delete(currentUserId: String, id: String) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Ticket with id $id not found") }

        checkDeleteAccess(currentUser, ticket)

        ticketRepository.delete(ticket)
    }

    private fun checkReadAccess(currentUser: User, ticket: Ticket): Boolean {
        if (ticket.ticketType == TicketType.APPEAL) {
            return currentUser.role == Role.GOD || currentUser.role == Role.BOSS
        }

        return when (currentUser.role) {
            Role.GOD -> true
            Role.MIGRANT -> ticket.author.id == currentUser.id
            Role.BOSS -> {
                val upkId = currentUser.upk?.id ?: return false
                ticket.subject.upk?.id == upkId
            }

            Role.INSPECTOR, Role.SECURITY -> {
                val upkId = currentUser.upk?.id ?: return false
                ticket.subject.upk?.id == upkId
            }
        }
    }

    private fun checkUpdateAccess(currentUser: User, ticket: Ticket) {
        if (ticket.ticketType == TicketType.APPEAL) {
            if (currentUser.role != Role.GOD && currentUser.role != Role.BOSS) {
                throw ForbiddenException("Only bosses and gods can update APPEAL tickets")
            }
            return
        }

        when (currentUser.role) {
            Role.GOD -> return
            Role.MIGRANT -> throw ForbiddenException("Migrants cannot update tickets")
            Role.BOSS -> {
                val upkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                if (ticket.subject.upk?.id != upkId) {
                    throw ForbiddenException("You can only update tickets for users in your UPK")
                }
            }

            Role.INSPECTOR, Role.SECURITY -> {
                val upkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Employee must be assigned to UPK")
                if (ticket.subject.upk?.id != upkId) {
                    throw ForbiddenException("You can only update tickets for users in your UPK")
                }
            }
        }
    }

    private fun checkDeleteAccess(currentUser: User, ticket: Ticket) {
        if (ticket.ticketType == TicketType.APPEAL) {
            if (currentUser.role != Role.GOD && currentUser.role != Role.BOSS) {
                throw ForbiddenException("Only bosses and gods can delete APPEAL tickets")
            }
            return
        }

        when (currentUser.role) {
            Role.GOD -> return
            Role.MIGRANT -> {
                if (ticket.author.id != currentUser.id) {
                    throw ForbiddenException("Migrants can only delete their own tickets")
                }
            }

            Role.BOSS -> return
            Role.INSPECTOR, Role.SECURITY -> throw ForbiddenException("You don't have permission to delete tickets")
        }
    }

    @Transactional
    fun addDocument(currentUserId: String, ticketId: String, documentId: String): TicketResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        checkUpdateAccess(currentUser, ticket)

        val document = documentRepository.findById(UUID.fromString(documentId))
            .orElseThrow { ResourceNotFoundException("Document with id $documentId not found") }

        ticket.documents.add(document)
        ticket.updatedAt = Instant.now()

        return ticketRepository.save(ticket).toResponse()
    }

    @Transactional
    fun removeDocument(currentUserId: String, ticketId: String, documentId: String) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        checkUpdateAccess(currentUser, ticket)

        val document = documentRepository.findById(UUID.fromString(documentId))
            .orElseThrow { ResourceNotFoundException("Document with id $documentId not found") }

        ticket.documents.remove(document)
        ticket.updatedAt = Instant.now()

        ticketRepository.save(ticket)
    }

    @Transactional(readOnly = true)
    fun getDocuments(currentUserId: String, ticketId: String): List<DocumentResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        if (!checkReadAccess(currentUser, ticket)) {
            throw ForbiddenException("You don't have access to this ticket")
        }

        return ticket.documents.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getDocumentsWithUpkCheck(currentUserId: String, ticketId: String): List<DocumentResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        val currentUserUpkId = currentUser.upk?.id
        val ticketUpkId = ticket.subject.upk?.id

        if (currentUser.role != Role.GOD) {
            if (currentUserUpkId == null || ticketUpkId == null || currentUserUpkId != ticketUpkId) {
                throw ForbiddenException("Access denied: You can only view documents from tickets in your UPK")
            }
        }

        return ticket.documents.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getRelatedTickets(currentUserId: String, ticketId: String): List<TicketResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        if (!checkReadAccess(currentUser, ticket)) {
            throw ForbiddenException("You don't have access to this ticket")
        }

        return ticket.relatedTickets.filter { checkReadAccess(currentUser, it) }.map { it.toResponse() }
    }

    @Transactional
    fun addRelatedTicket(currentUserId: String, ticketId: String, relatedTicketId: String): TicketResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        checkUpdateAccess(currentUser, ticket)

        val relatedTicket = ticketRepository.findById(UUID.fromString(relatedTicketId))
            .orElseThrow { ResourceNotFoundException("Related ticket with id $relatedTicketId not found") }

        ticket.relatedTickets.add(relatedTicket)
        ticket.updatedAt = Instant.now()

        return ticketRepository.save(ticket).toResponse()
    }

    @Transactional
    fun removeRelatedTicket(currentUserId: String, ticketId: String, relatedTicketId: String) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ticket = ticketRepository.findById(UUID.fromString(ticketId))
            .orElseThrow { ResourceNotFoundException("Ticket with id $ticketId not found") }

        checkUpdateAccess(currentUser, ticket)

        val relatedTicket = ticketRepository.findById(UUID.fromString(relatedTicketId))
            .orElseThrow { ResourceNotFoundException("Related ticket with id $relatedTicketId not found") }

        ticket.relatedTickets.remove(relatedTicket)
        ticket.updatedAt = Instant.now()

        ticketRepository.save(ticket)
    }
}
