package com.example.joy.data

import androidx.room.*

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Update
    fun update(user: User)

    @Query("SELECT * FROM UserTable WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String): User?

    @Query("SELECT * FROM UserTable")
    fun getUsers() : List<User>
}