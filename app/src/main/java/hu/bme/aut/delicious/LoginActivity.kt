package hu.bme.aut.delicious

import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.delicious.Data.CurrentUserManager
import hu.bme.aut.delicious.Data.Login.User
import hu.bme.aut.delicious.Data.Login.UserDatabase
import hu.bme.aut.delicious.fragments.RegisterDialogFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity(), RegisterDialogFragment.UserRegisterDialogListener {

    private lateinit var database: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        database = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "userDb"
        )
        .fallbackToDestructiveMigration()
        .build()

        initCurrentUser()

        btnLogin.setOnClickListener {
            when {
                login_username.text.toString().isEmpty() -> {
                    //TODO make a Snackbar
                    login_username.requestFocus()
                    //Snackbar.make(login_username, R.string.warn_enter_user, Snackbar.LENGTH_LONG).show()
                    Toast.makeText(this, R.string.warn_enter_user, Toast.LENGTH_LONG).show()
                }
                login_password.text.toString().isEmpty() -> {
                    login_password.requestFocus()
                    //Snackbar.make(login_username, R.string.warn_enter_password, Snackbar.LENGTH_LONG).show()
                    Toast.makeText(this, R.string.warn_enter_password, Toast.LENGTH_LONG).show()
                }
                else -> {
                    login()
                }
            }
        }

        btnRegister.setOnClickListener {
            showRegisterDialog()
        }
    }

    private fun initCurrentUser() {
        val pref = getSharedPreferences(CurrentUserManager.PREF_NAME, MODE_PRIVATE)
        val username = pref.getString("username", "")
        val pw = pref.getInt("password", 0)

        if(!username.isNullOrEmpty()){
            thread {
                val user = database.userDao().getUser(username)
                if (user != null && user.password == pw) {
                    runOnUiThread {
                        CurrentUserManager.CurrentUser = user
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            }
        }

    }

    private fun login() {
        thread {
            val user = database.userDao().getUser(login_username.text.toString())

            if(user == null){
                runOnUiThread {
                    login_username.requestFocus()
                    Toast.makeText(this, R.string.error_user, Toast.LENGTH_LONG).show()
                    return@runOnUiThread
                }
            }

            val pwhash = login_password.text.toString().hashCode()

            if(user!!.password == pwhash){
                val pref = getSharedPreferences("CurrentUser", MODE_PRIVATE)
                with (pref.edit()) {
                    putString("username", user.username)
                    putInt("password", user.password)
                    apply()
                }

                runOnUiThread {
                    CurrentUserManager.CurrentUser = user
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            else {
                val pref = getSharedPreferences("CurrentUser", MODE_PRIVATE)
                with (pref.edit()) {
                    putString("username", "")
                    putInt("password", 0)
                    apply()
                }

                runOnUiThread {
                    login_password.requestFocus()
                    Toast.makeText(this, R.string.error_password, Toast.LENGTH_LONG).show()
                    CurrentUserManager.CurrentUser = null
                }
            }
        }
    }

    private fun showRegisterDialog() {
        RegisterDialogFragment().show(
            supportFragmentManager,
            RegisterDialogFragment.TAG
        )
    }

    override fun onUserRegistered(newUser: User) {
        thread {
            try {
                database.userDao().insert(newUser)
                runOnUiThread{
                    Snackbar.make(login_username, "${newUser.username} was created!",Snackbar.LENGTH_LONG).show()
                    login_username.setText(newUser.username)
                }
            } catch (e: SQLiteConstraintException) {
                runOnUiThread{
                    Snackbar.make(login_username, R.string.error_user_taken, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}