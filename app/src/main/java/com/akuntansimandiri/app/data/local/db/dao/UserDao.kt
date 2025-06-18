package com.akuntansimandiri.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.akuntansimandiri.app.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :input OR name = :input LIMIT 1")
    suspend fun getUserByEmailOrName(input: String): UserEntity?

    @Query("SELECT * FROM users WHERE (email = :input OR name = :input) AND password = :password LIMIT 1")
    suspend fun login(input: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE (email = :input OR name = :input) AND password = :password LIMIT 1")
    suspend fun getUserByIdentifier(input: String, password: String): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET name = :name WHERE id = :userId")
    suspend fun updateName(userId: Int, name: String)

    @Query("UPDATE users SET email = :email WHERE id = :userId")
    suspend fun updateEmail(userId: Int, email: String)

    @Query("UPDATE users SET password = :password WHERE id = :userId")
    suspend fun updatePassword(userId: Int, password: String)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("UPDATE users SET password = :newPassword WHERE email = :email")
    suspend fun updatePasswordByEmail(email: String, newPassword: String)

}