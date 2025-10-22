package com.example.ibucket.Model

data class BucketModel(
    val bucketId: String = "",
    val ownerUid: String = "",
    val name: String = "",
    val heightMm: Int = 0,
    val thresholdPercent: Int = 50,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)