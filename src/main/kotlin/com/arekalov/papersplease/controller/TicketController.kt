package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tickets")
@PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
@Suppress("TooManyFunctions")
class TicketController(
    private val ticketService: TicketService,
) {

    @GetMapping
    suspend fun getAllTickets(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<TicketResponse>> {
        val response = ticketService.getAll(limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    suspend fun getTicketById(
        @PathVariable id: String,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-author/{authorId}")
    suspend fun getTicketsByAuthor(
        @PathVariable authorId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<TicketResponse>> {
        val response = ticketService.getByAuthor(authorId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-executor/{executorId}")
    suspend fun getTicketsByExecutor(
        @PathVariable executorId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<TicketResponse>> {
        val response = ticketService.getByExecutor(executorId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-status/{status}")
    suspend fun getTicketsByStatus(
        @PathVariable status: TicketStatus,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<TicketResponse>> {
        val response = ticketService.getByStatus(status, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-type/{type}")
    suspend fun getTicketsByType(
        @PathVariable type: TicketType,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<TicketResponse>> {
        val response = ticketService.getByType(type, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-priority/{priority}")
    suspend fun getTicketsByPriority(
        @PathVariable priority: Priority,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<TicketResponse>> {
        val response = ticketService.getByPriority(priority, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-shift/{shiftId}")
    suspend fun getTicketsByShift(
        @PathVariable shiftId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<TicketResponse>> {
        val response = ticketService.getByShift(shiftId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    suspend fun createTicket(
        @Valid @RequestBody request: TicketRequest,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    suspend fun updateTicket(
        @PathVariable id: String,
        @Valid @RequestBody request: TicketRequest,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.update(id, request)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    suspend fun partialUpdateTicket(
        @PathVariable id: String,
        @Valid @RequestBody request: TicketRequestPartial,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.partialUpdate(id, request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    suspend fun assignExecutor(
        @PathVariable id: String,
        @RequestParam executorId: String,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.assignExecutor(id, executorId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/close")
    suspend fun closeTicket(
        @PathVariable id: String,
        @RequestParam resolution: String,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.closeTicket(id, resolution)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/reopen")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    suspend fun reopenTicket(
        @PathVariable id: String,
    ): ResponseEntity<TicketResponse> {
        val response = ticketService.reopenTicket(id)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    suspend fun deleteTicket(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        ticketService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
