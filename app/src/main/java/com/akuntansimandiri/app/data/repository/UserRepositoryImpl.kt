package com.akuntansimandiri.app.data.repository

import com.akuntansimandiri.app.data.local.db.dao.UserDao
import com.akuntansimandiri.app.data.local.entity.UserEntity
import com.akuntansimandiri.app.domain.model.User
import com.akuntansimandiri.app.domain.repository.UserRepository

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun registerUser(user: User): Boolean {
        val entity = UserEntity(name = user.nama, email = user.email, password = user.password)
        userDao.insertUser(entity)
        return true
    }

    override suspend fun loginUser(identifier: String, password: String): User? {
        val entity = userDao.login(identifier, password)
        return entity?.let { User(it.id, it.name, it.email, it.password) }
    }

    override suspend fun getUser(identifier: String): User? {
        val entity = userDao.getUserByEmailOrName(identifier)
        return entity?.let { User(it.id, it.name, it.email, it.password) }
    }

    override suspend fun updateName(userId: Int, newName: String) {
        userDao.updateName(userId, newName)
    }

    override suspend fun updateEmail(userId: Int, newEmail: String) {
        userDao.updateEmail(userId, newEmail)
    }

    override suspend fun updatePassword(userId: Int, newPassword: String) {
        userDao.updatePassword(userId, newPassword)
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    override suspend fun updatePasswordByEmail(email: String, newPassword: String) {
        userDao.updatePasswordByEmail(email, newPassword)
    }


}



