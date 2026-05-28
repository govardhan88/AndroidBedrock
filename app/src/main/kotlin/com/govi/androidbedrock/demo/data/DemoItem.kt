package com.govi.androidbedrock.demo.data

import kotlinx.serialization.Serializable

@Serializable
data class DemoItem(
    val id: Int,
    val title: String,
    val description: String
)
