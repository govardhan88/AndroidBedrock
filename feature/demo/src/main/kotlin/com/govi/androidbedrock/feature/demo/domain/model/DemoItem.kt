package com.govi.androidbedrock.feature.demo.domain.model

/**
 * Domain model representing an interactive demo option available in the Bedrock demo showcase.
 */
data class DemoItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val badgeText: String,
    val features: List<String> = emptyList(),
)
