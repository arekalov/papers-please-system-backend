package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Document
import com.arekalov.papersplease.model.enums.DocumentType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
@Suppress("ForbiddenComment")
interface DocumentRepository : JpaRepository<Document, UUID> {
    fun findByDocumentType(type: DocumentType): List<Document>

    fun findByOwner_Id(ownerId: UUID, pageable: Pageable): Page<Document>

    /**
     * Получить все активные (не истекшие) документы пользователя
     * Использует PL/pgSQL функцию get_active_documents
     */
    @Query(value = "SELECT * FROM get_active_documents(CAST(:ownerId AS UUID))", nativeQuery = true)
    fun findActiveDocumentsByOwnerId(@Param("ownerId") ownerId: UUID): List<Document>

    /**
     * Получить все документы, прикрепленные к тикету
     * Использует PL/pgSQL функцию get_ticket_documents
     */
    @Query(
        value = "SELECT d.* FROM get_ticket_documents(CAST(:ticketId AS UUID)) td " +
            "JOIN documents d ON d.id = td.id",
        nativeQuery = true,
    )
    fun findDocumentsByTicketId(@Param("ticketId") ticketId: UUID): List<Document>
}
