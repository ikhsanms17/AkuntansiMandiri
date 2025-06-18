package com.akuntansimandiri.app.presentation.register.component

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akuntansimandiri.app.domain.model.User
import com.akuntansimandiri.app.domain.usecase.RegisterUseCase
import com.akuntansimandiri.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterState>(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnNamaChanged -> _uiState.value = _uiState.value.copy(nama = event.value)
            is RegisterEvent.OnEmailChanged -> _uiState.value = _uiState.value.copy(email = event.value)
            is RegisterEvent.OnPasswordChanged -> _uiState.value = _uiState.value.copy(password = event.value)
            is RegisterEvent.OnRepeatPasswordChanged -> _uiState.value = _uiState.value.copy(repeatPassword = event.value)
            is RegisterEvent.OnSubmit -> register()
        }
    }

    private fun register() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            Logger.d("register", "Memulai proses registrasi untuk email: ${state.email}, nama: ${state.nama}")

            val result = registerUseCase(
                user = User(
                    nama = state.nama,
                    email = state.email,
                    password = state.password
                ),
                repeatPassword = state.repeatPassword
            )

            result.onSuccess {
                Logger.d("register", "Registrasi berhasil untuk email: ${state.email}")
                _uiState.value = _uiState.value.copy(isSuccess = true, isLoading = false)
            }.onFailure {
                Logger.w("register", "Registrasi gagal untuk email: ${state.email}, error: ${it.message}")
                _uiState.value = _uiState.value.copy(error = it.message, isLoading = false)
            }
        }
    }
}