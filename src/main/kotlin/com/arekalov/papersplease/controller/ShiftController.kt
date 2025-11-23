package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.shift.ShiftRequest
import com.arekalov.papersplease.dto.shift.ShiftRequestPartial
import com.arekalov.papersplease.dto.shift.ShiftResponse
import com.arekalov.papersplease.service.ShiftService
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
import java.time.Instant

@RestController
@RequestMapping("/api/v1/shifts")
@PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
class ShiftController(
    private val shiftService: ShiftService,
) {

    @GetMapping
    fun getAllShifts(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<ShiftResponse>> {
        val response = shiftService.getAll(limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getShiftById(
        @PathVariable id: String,
    ): ResponseEntity<ShiftResponse> {
        val response = shiftService.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-upk/{upkId}")
    fun getShiftsByUpk(
        @PathVariable upkId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<ShiftResponse>> {
        val response = shiftService.getByUpk(upkId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-date")
    fun getShiftByDate(
        @RequestParam upkId: String,
        @RequestParam date: Instant,
    ): ResponseEntity<ShiftResponse?> {
        val response = shiftService.getByDate(upkId, date)
        return if (response != null) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun createShift(
        @Valid @RequestBody request: ShiftRequest,
    ): ResponseEntity<ShiftResponse> {
        val response = shiftService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun updateShift(
        @PathVariable id: String,
        @Valid @RequestBody request: ShiftRequest,
    ): ResponseEntity<ShiftResponse> {
        val response = shiftService.update(id, request)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun partialUpdateShift(
        @PathVariable id: String,
        @Valid @RequestBody request: ShiftRequestPartial,
    ): ResponseEntity<ShiftResponse> {
        val response = shiftService.partialUpdate(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun deleteShift(
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        shiftService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
