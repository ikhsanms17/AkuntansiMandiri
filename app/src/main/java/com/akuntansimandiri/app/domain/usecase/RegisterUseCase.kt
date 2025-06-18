package com.akuntansimandiri.app.domain.usecase

import com.akuntansimandiri.app.domain.model.User
import com.akuntansimandiri.app.domain.repository.UserRepository

class RegisterUseCase(private val repository: UserRepository) {

    suspend operator fun invoke(user: User, repeatPassword: String): Result<Unit> {
        if (user.nama.isBlank() || user.email.isBlank() || user.password.isBlank()) {
            return Result.failure(Exception("Semua field wajib diisi."))
        }

        if (user.password != repeatPassword) {
            return Result.failure(Exception("Password dan Ulangi Password harus sama."))
        }

        val success = repository.registerUser(user)
        return if (success) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Email atau nama sudah digunakan."))
        }
    }
}