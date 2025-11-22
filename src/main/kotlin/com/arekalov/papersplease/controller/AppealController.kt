package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.appeal.AppealRequest
import com.arekalov.papersplease.dto.appeal.AppealRequestPartial
import com.arekalov.papersplease.dto.appeal.AppealResponse
import com.arekalov.papersplease.model.enums.AppealDecision
import com.arekalov.papersplease.service.AppealService
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
@RequestMapping("/api/v1/appeals")
@PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
class AppealController(
    private val appealService: AppealService,
) {

    @GetMapping
    suspend fun getAllAppeals(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<AppealResponse>> {
        val response = appealService.getAll(limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    suspend fun getAppealById(
        @PathVariable id: String,
    ): ResponseEntity<AppealResponse> {
        val response = appealService.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-ticket/{ticketId}")
    suspend fun getAppealByTicket(
        @PathVariable ticketId: String,
    ): ResponseEntity<AppealResponse?> {
        val response = appealService.getByTicket(ticketId)
        return if (response != null) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/by-filed-by/{userId}")
    suspend fun getAppealsByFiledBy(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<AppealResponse>> {
        val response = appealService.getByFiledBy(userId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-decision/{decision}")
    suspend fun getAppealsByDecision(
        @PathVariable decision: AppealDecision,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<AppealResponse>> {
        val response = appealService.getByDecision(decision, limit, offset)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    suspend fun createAppeal(
        @Valid @RequestBody request: AppealRequest,
    ): ResponseEntity<AppealResponse> {
        val response = appealService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    suspend fun updateAppeal(
        @PathVariable id: String,
        @Valid @RequestBody request: AppealRequest,
    ): ResponseEntity<AppealResponse> {
        val response = appealService.update(id, request)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    suspend fun partialUpdateAppeal(
        @PathVariable id: String,
        @Valid @RequestBody request: AppealRequestPartial,
    ): ResponseEntity<AppealResponse> {
        val response = appealService.partialUpdate(id, request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/process")
    suspend fun processAppeal(
        @PathVariable id: String,
        @RequestParam decision: AppealDecision,
        @RequestParam decidedById: String,
        @RequestParam(required = false) notes: String?,
    ): ResponseEntity<AppealResponse> {
        val response = appealService.processAppeal(id, decision, decidedById, notes)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOD')")
    suspend fun deleteAppeal(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        appealService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
