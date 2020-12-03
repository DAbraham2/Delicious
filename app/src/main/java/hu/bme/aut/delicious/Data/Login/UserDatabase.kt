package hu.bme.aut.delicious.Data.Login

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [User::class], version = 2)
abstract class UserDatabase:RoomDatabase() {
    abstract fun userDao(): UserDAO
}