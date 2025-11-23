package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.participation.ParticipationRequest
import com.arekalov.papersplease.dto.participation.ParticipationRequestPartial
import com.arekalov.papersplease.dto.participation.ParticipationResponse
import com.arekalov.papersplease.service.ParticipationService
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
@RequestMapping("/api/v1/participations")
class ParticipationController(
    private val participationService: ParticipationService,
) {

    @GetMapping
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
    fun getAllParticipations(
        authentication: Authentication,
        @RequestParam(required = false) shiftId: String?,
        @RequestParam(required = false) userId: String?,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<ParticipationResponse>> {
        val response = when {
            shiftId != null -> participationService.getByShift(authentication.name, shiftId, limit, offset)
            userId != null -> participationService.getByUser(authentication.name, userId, limit, offset)
            else -> participationService.getAll(authentication.name, limit, offset)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
    fun getParticipationById(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<ParticipationResponse> {
        val response = participationService.getById(authentication.name, id)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun createParticipation(
        authentication: Authentication,
        @Valid @RequestBody request: ParticipationRequest,
    ): ResponseEntity<ParticipationResponse> {
        val response = participationService.create(authentication.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun partialUpdateParticipation(
        authentication: Authentication,
        @PathVariable id: String,
        @Valid @RequestBody request: ParticipationRequestPartial,
    ): ResponseEntity<ParticipationResponse> {
        val response = participationService.partialUpdate(authentication.name, id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun deleteParticipation(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        participationService.delete(authentication.name, id)
        return ResponseEntity.noContent().build()
    }
}
