package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.document.DocumentRequest
import com.arekalov.papersplease.dto.document.DocumentRequestPartial
import com.arekalov.papersplease.dto.document.DocumentResponse
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.enums.DocumentType
import com.arekalov.papersplease.repository.DocumentRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(limit: Int, offset: Int): PagedResponse<DocumentResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = documentRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: String): DocumentResponse {
        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }
        return document.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByType(type: DocumentType, limit: Int, offset: Int): PagedResponse<DocumentResponse> {
        val documents = documentRepository.findByDocumentType(type)
        val totalCount = documents.size.toLong()

        val paginatedDocuments = documents
            .drop(offset)
            .take(limit)

        return PagedResponse(
            items = paginatedDocuments.map { it.toResponse() },
            total = totalCount,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional
    fun create(request: DocumentRequest): DocumentResponse {
        val document = request.toEntity()
        return documentRepository.save(document).toResponse()
    }

    @Transactional
    fun update(id: String, request: DocumentRequest): DocumentResponse {
        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }

        document.apply {
            documentType = request.documentType
            issuedAt = request.validFrom ?: issuedAt
            expiresAt = request.validUntil
        }

        return documentRepository.save(document).toResponse()
    }

    @Transactional
    fun partialUpdate(id: String, request: DocumentRequestPartial): DocumentResponse {
        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }

        request.documentType?.let { document.documentType = it }
        request.validFrom?.let { document.issuedAt = it }
        request.validUntil?.let { document.expiresAt = it }

        return documentRepository.save(document).toResponse()
    }

    @Transactional
    fun delete(id: String) {
        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }
        documentRepository.delete(document)
    }
}
