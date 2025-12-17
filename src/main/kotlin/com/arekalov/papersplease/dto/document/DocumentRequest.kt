package com.arekalov.papersplease.dto.document

import com.arekalov.papersplease.model.enums.DocumentType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class DocumentRequest(
    @field:NotBlank(message = "User ID is required")
    val userId: String,

    @field:NotNull(message = "Document type is required")
    val documentType: DocumentType,

    @field:NotNull(message = "Document body is required")
    val body: Map<String, Any>,

    val validFrom: Instant? = null,

    val validUntil: Instant? = null,

    val attachToProfile: Boolean = false,
)
