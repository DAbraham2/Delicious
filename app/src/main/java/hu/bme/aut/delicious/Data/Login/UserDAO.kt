package hu.bme.aut.delicious.Data.Login

import androidx.room.*

@Dao
interface UserDAO {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Insert
    fun insert(user: User): Long

    @Update
    fun update(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM user WHERE username = :username")
    fun getUser(username: String): User?
}