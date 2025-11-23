package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.participation.ParticipationRequest
import com.arekalov.papersplease.dto.participation.ParticipationRequestPartial
import com.arekalov.papersplease.dto.participation.ParticipationResponse
import com.arekalov.papersplease.model.enums.Specialization
import com.arekalov.papersplease.service.ParticipationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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
@RequestMapping("/api/v1/participationsKate")
@PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
class ParticipationController(
    private val participationService: ParticipationService,
) {

    @GetMapping
    fun getAllParticipations(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<ParticipationResponse>> {
        val response = participationService.getAll(limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getParticipationById(
        @PathVariable id: String,
    ): ResponseEntity<ParticipationResponse> {
        val response = participationService.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-shift/{shiftId}")
    fun getParticipationsByShift(
        @PathVariable shiftId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<ParticipationResponse>> {
        val response = participationService.getByShift(shiftId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-user/{userId}")
    fun getParticipationsByUser(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<ParticipationResponse>> {
        val response = participationService.getByUser(userId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-specialization/{specialization}")
    fun getParticipationsBySpecialization(
        @PathVariable specialization: Specialization,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<ParticipationResponse>> {
        val response = participationService.getBySpecialization(specialization, limit, offset)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun createParticipation(
        @Valid @RequestBody request: ParticipationRequest,
    ): ResponseEntity<ParticipationResponse> {
        val response = participationService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun partialUpdateParticipation(
        @PathVariable id: String,
        @Valid @RequestBody request: ParticipationRequestPartial,
    ): ResponseEntity<ParticipationResponse> {
        val response = participationService.partialUpdate(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun deleteParticipation(
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        participationService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
