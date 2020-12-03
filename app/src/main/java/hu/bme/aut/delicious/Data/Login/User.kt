package hu.bme.aut.delicious.Data.Login

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user", indices = [ Index(value = [ "username" ], unique = true)])
data class User (
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "username") @NonNull val username: String,
    @ColumnInfo(name = "password") @NonNull val password: Int
)