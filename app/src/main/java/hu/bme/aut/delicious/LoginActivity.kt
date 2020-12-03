package hu.bme.aut.delicious

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.delicious.Data.CurrentUserManager
import hu.bme.aut.delicious.Data.Login.UserDatabase
import kotlinx.android.synthetic.main.activity_login.*
import java.util.logging.Level
import java.util.logging.Logger

class LoginActivity : AppCompatActivity() {

    private lateinit var database: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        database = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "userDb"
        ).build()

        initCurrentUser()

        btnLogin.setOnClickListener {
            when {
                login_username.text.toString().isEmpty() -> {
                    //TODO make a Snackbar
                    login_username.requestFocus()
                    login_username.error = "Please enter your username"
                }
                login_password.text.toString().isEmpty() -> {
                    login_password.requestFocus()
                    login_password.error = "Please enter your password"
                }
                else -> {
                    login()
                }
            }
        }

        btnRegister.setOnClickListener {
            //TODO register
        }
    }

    private fun initCurrentUser() {
        val pref = getSharedPreferences(CurrentUserManager.PREF_NAME, MODE_PRIVATE)
        val username = pref.getString("username", "")
        val pw = pref.getInt("password", 0)

        if(!username.isNullOrEmpty()){
            val user = database.userDao().getUser(username)
            if (user != null && user.password == pw) {
                CurrentUserManager.CurrentUser = user
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }

    private fun login() {
        val user = database.userDao().getUser(login_username.text.toString())

        if(user == null){
            login_username.requestFocus()
            login_username.error = "Invalid user!"
            return
        }

        val pwhash = login_password.text.toString().hashCode()

        if(user.password == pwhash){
            val pref = getSharedPreferences("CurrentUser", MODE_PRIVATE)
            with (pref.edit()) {
                putString("username", user.username)
                putInt("password", user.password)
                apply()
            }
            CurrentUserManager.CurrentUser = user
            startActivity(Intent(this, MainActivity::class.java))
        }
        else {
            login_password.requestFocus()
            login_password.error = "wrong password!"
            CurrentUserManager.CurrentUser = null

            val pref = getSharedPreferences("CurrentUser", MODE_PRIVATE)
            with (pref.edit()) {
                putString("username", "")
                putInt("password", 0)
                apply()
            }
        }
    }
}