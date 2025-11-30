package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.shift.ShiftDetailedResponse
import com.arekalov.papersplease.dto.shift.ShiftRequest
import com.arekalov.papersplease.dto.shift.ShiftRequestPartial
import com.arekalov.papersplease.dto.shift.ShiftResponse
import com.arekalov.papersplease.service.ShiftService
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
@RequestMapping("/api/v1/shifts")
@PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
class ShiftController(
    private val shiftService: ShiftService,
) {

    @GetMapping
    fun getAllShifts(
        authentication: Authentication,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<ShiftResponse>> {
        val response = shiftService.getAll(authentication.name, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getShiftById(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<ShiftResponse> {
        val response = shiftService.getById(authentication.name, id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/details")
    fun getShiftDetailsById(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<ShiftDetailedResponse> {
        val response = shiftService.getDetailedById(authentication.name, id)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun createShift(
        authentication: Authentication,
        @Valid @RequestBody request: ShiftRequest,
    ): ResponseEntity<ShiftResponse> {
        val response = shiftService.create(authentication.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun partialUpdateShift(
        authentication: Authentication,
        @PathVariable id: String,
        @Valid @RequestBody request: ShiftRequestPartial,
    ): ResponseEntity<ShiftResponse> {
        val response = shiftService.partialUpdate(authentication.name, id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun deleteShift(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        shiftService.delete(authentication.name, id)
        return ResponseEntity.noContent().build()
    }
}
