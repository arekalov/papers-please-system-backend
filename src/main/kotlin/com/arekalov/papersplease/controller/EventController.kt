package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.event.EventRequest
import com.arekalov.papersplease.dto.event.EventRequestPartial
import com.arekalov.papersplease.dto.event.EventResponse
import com.arekalov.papersplease.service.EventService
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
@RequestMapping("/api/v1/events")
class EventController(
    private val eventService: EventService,
) {

    @GetMapping
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
    fun getAllEvents(
        authentication: Authentication,
        @RequestParam(required = false) shiftId: String?,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<EventResponse>> {
        val response = if (shiftId != null) {
            eventService.getByShift(authentication.name, shiftId, limit, offset)
        } else {
            eventService.getAll(authentication.name, limit, offset)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
    fun getEventById(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<EventResponse> {
        val response = eventService.getById(authentication.name, id)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasRole('GOD')")
    fun createEvent(
        authentication: Authentication,
        @Valid @RequestBody request: EventRequest,
    ): ResponseEntity<EventResponse> {
        val response = eventService.create(authentication.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun partialUpdateEvent(
        authentication: Authentication,
        @PathVariable id: String,
        @Valid @RequestBody request: EventRequestPartial,
    ): ResponseEntity<EventResponse> {
        val response = eventService.partialUpdate(authentication.name, id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOD')")
    fun deleteEvent(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        eventService.delete(authentication.name, id)
        return ResponseEntity.noContent().build()
    }
}
