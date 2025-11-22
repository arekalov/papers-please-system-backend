package com.arekalov.papersplease.dto

data class PagedResponse<T>(
    val items: List<T>,
    val total: Long,
    val limit: Int,
    val offset: Int,
)
