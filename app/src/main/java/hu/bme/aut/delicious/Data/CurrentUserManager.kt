package hu.bme.aut.delicious.Data

import hu.bme.aut.delicious.Data.Login.User

object CurrentUserManager {
    var CurrentUser: User? = null

    const val PREF_NAME = "CurrentUser"
}