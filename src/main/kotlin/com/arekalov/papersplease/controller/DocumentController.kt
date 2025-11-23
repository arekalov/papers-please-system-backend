package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.document.DocumentRequest
import com.arekalov.papersplease.dto.document.DocumentRequestPartial
import com.arekalov.papersplease.dto.document.DocumentResponse
import com.arekalov.papersplease.service.DocumentService
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
@RequestMapping("/api/v1/documents")
class DocumentController(
    private val documentService: DocumentService,
) {

    @GetMapping
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD', 'MIGRANT')")
    fun getAllDocumentsByUserId(
        authentication: Authentication,
        @RequestParam userId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<DocumentResponse>> {
        val response = documentService.getAllByUserId(authentication.name, userId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD', 'MIGRANT')")
    fun getDocumentById(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<DocumentResponse> {
        val response = documentService.getById(authentication.name, id)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MIGRANT', 'GOD')")
    fun createDocument(
        authentication: Authentication,
        @Valid @RequestBody request: DocumentRequest,
    ): ResponseEntity<DocumentResponse> {
        val response = documentService.create(authentication.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('MIGRANT', 'GOD')")
    fun partialUpdateDocument(
        authentication: Authentication,
        @PathVariable id: String,
        @Valid @RequestBody request: DocumentRequestPartial,
    ): ResponseEntity<DocumentResponse> {
        val response = documentService.partialUpdate(authentication.name, id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOD')")
    fun deleteDocument(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        documentService.delete(authentication.name, id)
        return ResponseEntity.noContent().build()
    }
}
