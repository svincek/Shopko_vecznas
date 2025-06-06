package com.example.shopko.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Int,
    val size: Int,
    val number: Int
)