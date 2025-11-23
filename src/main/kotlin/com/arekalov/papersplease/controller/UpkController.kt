package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.upk.UpkRequest
import com.arekalov.papersplease.dto.upk.UpkRequestPartial
import com.arekalov.papersplease.dto.upk.UpkResponse
import com.arekalov.papersplease.service.UpkService
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
@RequestMapping("/api/v1/upks")
class UpkController(
    private val upkService: UpkService,
) {

    @GetMapping
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun getAllUpks(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<UpkResponse>> {
        val response = upkService.getAll(limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun getUpkById(
        @PathVariable id: String,
    ): ResponseEntity<UpkResponse> {
        val response = upkService.getById(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun createUpk(
        @Valid @RequestBody request: UpkRequest,
    ): ResponseEntity<UpkResponse> {
        val response = upkService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun updateUpk(
        @PathVariable id: String,
        @Valid @RequestBody request: UpkRequest,
    ): ResponseEntity<UpkResponse> {
        val response = upkService.update(id, request)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun partialUpdateUpk(
        @PathVariable id: String,
        @Valid @RequestBody request: UpkRequestPartial,
    ): ResponseEntity<UpkResponse> {
        val response = upkService.partialUpdate(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOD')")
    fun deleteUpk(
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        upkService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
