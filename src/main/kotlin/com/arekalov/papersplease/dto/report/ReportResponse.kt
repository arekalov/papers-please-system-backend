package com.arekalov.papersplease.dto.report

import java.time.Instant

data class ReportResponse(
    val reportType: String,
    val generatedAt: Instant,
    val data: Map<String, Any>,
    val summary: String? = null,
)
