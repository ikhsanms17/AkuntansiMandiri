package com.akuntansimandiri.app.domain.model

data class User(
    val id: Int = 0,
    val nama: String,
    val email: String,
    val password: String
)