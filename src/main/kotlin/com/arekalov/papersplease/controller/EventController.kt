package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.event.EventRequest
import com.arekalov.papersplease.dto.event.EventRequestPartial
import com.arekalov.papersplease.dto.event.EventResponse
import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.service.EventService
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
@RequestMapping("/api/v1/events")
@PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
class EventController(
    private val eventService: EventService,
) {

    @GetMapping
    suspend fun getAllEvents(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<EventResponse>> {
        val response = eventService.getAll(limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    suspend fun getEventById(
        @PathVariable id: String,
    ): ResponseEntity<EventResponse> {
        val response = eventService.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-shift/{shiftId}")
    suspend fun getEventsByShift(
        @PathVariable shiftId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<EventResponse>> {
        val response = eventService.getByShift(shiftId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-priority/{priority}")
    suspend fun getEventsByPriority(
        @PathVariable priority: Priority,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<EventResponse>> {
        val response = eventService.getByPriority(priority, limit, offset)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    suspend fun createEvent(
        @Valid @RequestBody request: EventRequest,
    ): ResponseEntity<EventResponse> {
        val response = eventService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    suspend fun updateEvent(
        @PathVariable id: String,
        @Valid @RequestBody request: EventRequest,
    ): ResponseEntity<EventResponse> {
        val response = eventService.update(id, request)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    suspend fun partialUpdateEvent(
        @PathVariable id: String,
        @Valid @RequestBody request: EventRequestPartial,
    ): ResponseEntity<EventResponse> {
        val response = eventService.partialUpdate(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    suspend fun deleteEvent(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        eventService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
