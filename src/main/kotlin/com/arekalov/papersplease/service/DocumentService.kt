package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.document.DocumentRequest
import com.arekalov.papersplease.dto.document.DocumentRequestPartial
import com.arekalov.papersplease.dto.document.DocumentResponse
import com.arekalov.papersplease.exception.ForbiddenException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Document
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.repository.DocumentRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getAllByUserId(
        currentUserId: String,
        userId: String,
        limit: Int,
        offset: Int,
    ): PagedResponse<DocumentResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val targetUserId = UUID.fromString(userId)
        val targetUser = userRepository.findById(targetUserId)
            .orElseThrow { ResourceNotFoundException("User with id $userId not found") }

        checkReadAccessByUserId(currentUser, targetUser)

        val pageable = PageRequest.of(offset / limit, limit)
        val page = documentRepository.findByOwner_Id(targetUserId, pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getActiveDocumentsByUserId(
        currentUserId: String,
        userId: String,
    ): List<DocumentResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val targetUserId = UUID.fromString(userId)
        val targetUser = userRepository.findById(targetUserId)
            .orElseThrow { ResourceNotFoundException("User with id $userId not found") }

        checkReadAccessByUserId(currentUser, targetUser)

        val activeDocuments = documentRepository.findActiveDocumentsByOwnerId(targetUserId)

        return activeDocuments.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getDocumentsByTicketId(
        currentUserId: String,
        ticketId: String,
    ): List<DocumentResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val documents = documentRepository.findDocumentsByTicketId(UUID.fromString(ticketId))

        documents.forEach { document ->
            checkReadAccessToDocument(currentUser, document)
        }

        return documents.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getById(currentUserId: String, id: String): DocumentResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }

        checkReadAccessToDocument(currentUser, document)

        return document.toResponse()
    }

    @Transactional
    fun create(currentUserId: String, request: DocumentRequest): DocumentResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val ownerId = UUID.fromString(request.userId)
        val owner = userRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner user with id ${request.userId} not found") }

        checkCreateAccess(currentUser, owner)

        val document = request.toEntity(owner)
        return documentRepository.save(document).toResponse()
    }

    @Transactional
    fun partialUpdate(currentUserId: String, id: String, request: DocumentRequestPartial): DocumentResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }

        checkUpdateAccess(currentUser, document)

        request.documentType?.let { document.documentType = it }
        request.body?.let {
            document.body = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().writeValueAsString(it)
        }
        request.validFrom?.let { document.issuedAt = it }
        request.validUntil?.let { document.expiresAt = it }

        return documentRepository.save(document).toResponse()
    }

    @Transactional
    fun delete(currentUserId: String, id: String) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        if (currentUser.role != Role.GOD) {
            throw ForbiddenException("Only GOD can delete documents")
        }

        val document = documentRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Document with id $id not found") }
        documentRepository.delete(document)
    }

    private fun checkReadAccessByUserId(currentUser: User, targetUser: User) {
        when (currentUser.role) {
            Role.GOD, Role.INSPECTOR, Role.SECURITY, Role.BOSS -> return
            Role.MIGRANT -> {
                if (currentUser.id != targetUser.id) {
                    throw ForbiddenException("Migrants can only view their own documents")
                }
            }
        }
    }

    private fun checkReadAccessToDocument(currentUser: User, document: Document) {
        when (currentUser.role) {
            Role.GOD, Role.INSPECTOR, Role.SECURITY, Role.BOSS -> return
            Role.MIGRANT -> {
                if (currentUser.id != document.owner.id) {
                    throw ForbiddenException("Migrants can only view their own documents")
                }
            }
        }
    }

    private fun checkCreateAccess(currentUser: User, owner: User) {
        when (currentUser.role) {
            Role.GOD -> return
            Role.MIGRANT -> {
                if (currentUser.id != owner.id) {
                    throw ForbiddenException("Migrants can only create documents for themselves")
                }
            }
            else -> throw ForbiddenException("Only migrants and GOD can create documents")
        }
    }

    private fun checkUpdateAccess(currentUser: User, document: Document) {
        when (currentUser.role) {
            Role.GOD -> return
            Role.MIGRANT -> {
                if (currentUser.id != document.owner.id) {
                    throw ForbiddenException("Migrants can only update their own documents")
                }
            }
            else -> throw ForbiddenException("Only migrants and GOD can update documents")
        }
    }
}
