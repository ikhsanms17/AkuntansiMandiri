package com.akuntansimandiri.app.presentation.register.component

sealed class RegisterEvent {
    data class OnNamaChanged(val value: String) : RegisterEvent()
    data class OnEmailChanged(val value: String) : RegisterEvent()
    data class OnPasswordChanged(val value: String) : RegisterEvent()
    data class OnRepeatPasswordChanged(val value: String) : RegisterEvent()
    object OnSubmit : RegisterEvent()
}