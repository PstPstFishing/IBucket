package com.example.ibucket.Model

data class UserModel(
    val uid: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)