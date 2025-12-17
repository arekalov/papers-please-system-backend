package com.arekalov.papersplease.dto.document

import com.arekalov.papersplease.model.enums.DocumentType
import java.time.Instant

data class DocumentResponse(
    val id: String,
    val userId: String,
    val documentType: DocumentType,
    val body: Map<String, Any>,
    val validFrom: Instant? = null,
    val validUntil: Instant? = null,
    val attachToProfile: Boolean = false,
)
