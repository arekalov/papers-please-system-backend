package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.document.DocumentRequest
import com.arekalov.papersplease.dto.document.DocumentRequestPartial
import com.arekalov.papersplease.dto.document.DocumentResponse
import com.arekalov.papersplease.model.enums.DocumentType
import com.arekalov.papersplease.service.DocumentService
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
@RequestMapping("/api/v1/documents")
@PreAuthorize("hasAnyRole('INSPECTOR', 'BOSS', 'SECURITY', 'GOD')")
class DocumentController(
    private val documentService: DocumentService,
) {

    @GetMapping
    fun getAllDocuments(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<DocumentResponse>> {
        val response = documentService.getAll(limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getDocumentById(
        @PathVariable id: String,
    ): ResponseEntity<DocumentResponse> {
        val response = documentService.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-type/{type}")
    fun getDocumentsByType(
        @PathVariable type: DocumentType,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<DocumentResponse>> {
        val response = documentService.getByType(type, limit, offset)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createDocument(
        @Valid @RequestBody request: DocumentRequest,
    ): ResponseEntity<DocumentResponse> {
        val response = documentService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun partialUpdateDocument(
        @PathVariable id: String,
        @Valid @RequestBody request: DocumentRequestPartial,
    ): ResponseEntity<DocumentResponse> {
        val response = documentService.partialUpdate(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun deleteDocument(
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        documentService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
