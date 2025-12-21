package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.document.DocumentResponse
import com.arekalov.papersplease.dto.ticket.DelegateTicketRequest
import com.arekalov.papersplease.dto.ticket.TicketDetailedResponse
import com.arekalov.papersplease.dto.ticket.TicketRequest
import com.arekalov.papersplease.dto.ticket.TicketRequestPartial
import com.arekalov.papersplease.dto.ticket.TicketResponse
import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.TicketStatus
import com.arekalov.papersplease.model.enums.TicketType
import com.arekalov.papersplease.service.TicketService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tickets")
class TicketController(
    private val ticketService: TicketService,
) {

    @GetMapping
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    @Suppress("LongParameterList")
    fun getAllTickets(
        authentication: Authentication,
        @RequestParam(required = false) authorId: String?,
        @RequestParam(required = false) executorId: String?,
        @RequestParam(required = false) status: TicketStatus?,
        @RequestParam(required = false) type: TicketType?,
        @RequestParam(required = false) priority: Priority?,
        @RequestParam(required = false) shiftId: String?,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<TicketResponse>> {
        val response = when {
            authorId != null -> ticketService.getByAuthor(authentication.name, authorId, limit, offset)
            executorId != null -> ticketService.getByExecutor(authentication.name, executorId, limit, offset)
            status != null -> ticketService.getByStatus(authentication.name, status, limit, offset)
            type != null -> ticketService.getByType(authentication.name, type, limit, offset)
            priority != null -> ticketService.getByPriority(authentication.name, priority, limit, offset)
            shiftId != null -> ticketService.getByShift(authentication.name, shiftId, limit, offset)
            else -> ticketService.getAll(authentication.name, limit, offset)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    fun getTicketById(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<TicketDetailedResponse> {
        val response = ticketService.getByIdDetailed(authentication.name, id)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    fun createTicket(
        authentication: Authentication,
        @Valid @RequestBody request: TicketRequest,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.create(authentication.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    fun partialUpdateTicket(
        authentication: Authentication,
        @PathVariable id: String,
        @Valid @RequestBody request: TicketRequestPartial,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.partialUpdate(authentication.name, id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MIGRANT', 'BOSS', 'GOD')")
    fun deleteTicket(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        ticketService.delete(authentication.name, id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    fun getTicketDocuments(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<List<DocumentResponse>> {
        val response = ticketService.getDocumentsWithUpkCheck(authentication.name, id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/documents/{documentId}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    fun addDocumentToTicket(
        authentication: Authentication,
        @PathVariable id: String,
        @PathVariable documentId: String,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.addDocument(authentication.name, id, documentId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    fun removeDocumentFromTicket(
        authentication: Authentication,
        @PathVariable id: String,
        @PathVariable documentId: String,
    ): ResponseEntity<Unit> {
        ticketService.removeDocument(authentication.name, id, documentId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/related")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    fun getRelatedTickets(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<List<TicketResponse>> {
        val response = ticketService.getRelatedTickets(authentication.name, id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/related/{relatedTicketId}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    fun addRelatedTicket(
        authentication: Authentication,
        @PathVariable id: String,
        @PathVariable relatedTicketId: String,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.addRelatedTicket(authentication.name, id, relatedTicketId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}/related/{relatedTicketId}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'MIGRANT', 'GOD')")
    fun removeRelatedTicket(
        authentication: Authentication,
        @PathVariable id: String,
        @PathVariable relatedTicketId: String,
    ): ResponseEntity<Unit> {
        ticketService.removeRelatedTicket(authentication.name, id, relatedTicketId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/delegate")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
    fun delegateTicket(
        authentication: Authentication,
        @PathVariable id: String,
        @Valid @RequestBody request: DelegateTicketRequest,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.delegateTicket(authentication.name, id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
