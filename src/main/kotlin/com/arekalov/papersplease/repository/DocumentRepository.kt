package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Document
import com.arekalov.papersplease.model.enums.DocumentType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DocumentRepository : JpaRepository<Document, UUID> {
    fun findByDocumentType(type: DocumentType): List<Document>

    fun findByOwner_Id(ownerId: UUID, pageable: Pageable): Page<Document>
}
