package hu.bme.aut.delicious.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import hu.bme.aut.delicious.Data.Login.User
import hu.bme.aut.delicious.R
import java.lang.RuntimeException

class RegisterDialogFragment : DialogFragment() {

    private lateinit var usernameET: EditText
    private lateinit var passwordET: EditText
    private lateinit var password_confET: EditText

    interface UserRegisterDialogListener {
        fun onUserRegistered(newUser: User)
    }

    private lateinit var listener: UserRegisterDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as? UserRegisterDialogListener
            ?: throw RuntimeException("Activity must implement UserRegisterDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(getContentView())
            .setPositiveButton("Save") { dialogInterface, i ->
                if(isValid()) {
                    listener.onUserRegistered(getUser())
                }
                else{
                    Toast.makeText(requireContext(), "Invalid input!", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun isValid() = usernameET.text.isNotEmpty() && passwordET.text.isNotEmpty() && passwordET.text.toString().hashCode() == password_confET.text.toString().hashCode()

    private fun getUser() = User(
        id = null,
        username = usernameET.text.toString(),
        password = passwordET.text.toString().hashCode()
    )

    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.register_user, null)

        usernameET = contentView.findViewById(R.id.username)
        passwordET = contentView.findViewById(R.id.password)
        password_confET = contentView.findViewById(R.id.password_conf)

        return contentView
    }

    companion object{
        const val TAG = "RegisterDialogFragment"
    }
}