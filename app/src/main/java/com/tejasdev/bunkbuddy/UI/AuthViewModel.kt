package com.tejasdev.bunkbuddy.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.util.Log

import androidx.lifecycle.AndroidViewModel
import com.tejasdev.bunkbuddy.datamodel.User
import com.tejasdev.bunkbuddy.repository.AuthRepository
import com.tejasdev.bunkbuddy.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val app: Application,
    private val repo: AuthRepository
): AndroidViewModel(app) {

    private val session = Session.getInstance(app.applicationContext)

    fun updateImage(image: String){
        session.updateUserImage(image)
    }
    fun updatePassword(newPass: String) = session.updatePassword(newPass)
    fun getPassword(): String = session.getPassword()
    fun updateUserName(username: String) {
        session.updateUserName(username)
    }

    fun isLogin():Boolean = session.isLogin()

    fun createSession(user: User){
        Log.w("image-upload", "session create : ${user.image}")
        user.apply {
            session.createSession(name, email, id, image, password)
        }
    }
    fun getUserImage(): Uri = session.getUserImage()
    fun getUserName(): String = session.getUserName()
    fun getEmail(): String = session.getEmail()
    fun loginUser(email: String, password: String, callback:(User?, String?)->Unit){
        repo.login(email, password){ user, message ->
            callback(user, message)
        }
    }
    fun signOut(){
       session.signOut()
    }
    fun signupUser(email: String, name: String, password: String, image: String, callback: (User?, String?)->Unit){
        repo.signup(name, email, password, image){user, message ->
            callback(user, message)
        }
    }
    fun updateUserDetails(
        name: String,
        image: String,
        callback: (User?, String?) -> Unit
    ){
        repo.updateUser(
            email = getEmail(),
            password = getPassword(),
            username = name,
            image = image
        ){ user, message ->
            user?.let{
                updateUserName(user.name)
                updateImage(user.image)
            }
            callback(user, message)
        }
    }

    fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork?:return false
        val capability = connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
        return when{
            capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}