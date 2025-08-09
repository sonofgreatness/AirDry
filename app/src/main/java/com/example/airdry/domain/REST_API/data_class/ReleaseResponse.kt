package com.example.airdry.domain.REST_API.data_class

data class ReleaseResponse(
    val tag_name: String,
    val published_at: String,
    val assets: List<Asset>
)