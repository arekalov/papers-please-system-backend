package com.arekalov.papersplease.dto.document

import com.arekalov.papersplease.model.enums.DocumentType
import java.time.Instant

data class DocumentRequestPartial(
    val documentType: DocumentType? = null,
    val body: Map<String, Any>? = null,
    val validFrom: Instant? = null,
    val validUntil: Instant? = null,
)
