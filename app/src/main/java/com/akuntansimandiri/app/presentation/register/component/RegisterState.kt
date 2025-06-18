package com.akuntansimandiri.app.presentation.register.component

data class RegisterState(
    val nama: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)