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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
) {

    @Transactional(readOnly = true)
    suspend fun getAll(limit: Int, offset: Int): PagedResponse<DocumentResponse> = withContext(Dispatchers.IO) {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = documentRepository.findAll(pageable)

        PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    suspend fun getById(id: String): DocumentResponse = withContext(Dispatchers.IO) {
        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }
        document.toResponse()
    }

    @Transactional(readOnly = true)
    suspend fun getByType(type: DocumentType, limit: Int, offset: Int): PagedResponse<DocumentResponse> =
        withContext(Dispatchers.IO) {
            val documents = documentRepository.findByDocumentType(type)
            val totalCount = documents.size.toLong()

            val paginatedDocuments = documents
                .drop(offset)
                .take(limit)

            PagedResponse(
                items = paginatedDocuments.map { it.toResponse() },
                total = totalCount,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional
    suspend fun create(request: DocumentRequest): DocumentResponse = withContext(Dispatchers.IO) {
        val document = request.toEntity()
        documentRepository.save(document).toResponse()
    }

    @Transactional
    suspend fun update(id: String, request: DocumentRequest): DocumentResponse = withContext(Dispatchers.IO) {
        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }

        document.apply {
            documentType = request.documentType
            issuedAt = request.validFrom ?: issuedAt
            expiresAt = request.validUntil
        }

        documentRepository.save(document).toResponse()
    }

    @Transactional
    suspend fun partialUpdate(id: String, request: DocumentRequestPartial): DocumentResponse =
        withContext(Dispatchers.IO) {
            val document = documentRepository.findById(UUID.fromString(id))
                .orElseThrow { ResourceNotFoundException("Document with id $id not found") }

            request.documentType?.let { document.documentType = it }
            request.validFrom?.let { document.issuedAt = it }
            request.validUntil?.let { document.expiresAt = it }

            documentRepository.save(document).toResponse()
        }

    @Transactional
    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }
        documentRepository.delete(document)
    }
}
