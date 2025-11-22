package com.arekalov.papersplease.dto

data class PagedResponse<T>(
    val total: Int,
    val limit: Int,
    val offset: Int,
    val items: List<T>,
)
