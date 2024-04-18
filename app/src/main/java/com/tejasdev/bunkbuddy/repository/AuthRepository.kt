package com.tejasdev.bunkbuddy.repository

import android.util.Log
import com.google.gson.Gson
import com.tejasdev.bunkbuddy.api.AuthAPI
import com.tejasdev.bunkbuddy.datamodel.MessageResponse
import com.tejasdev.bunkbuddy.datamodel.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthAPI
){
    fun login(email: String, password: String, callback: (User?, String?)-> Unit){
        val call = api.loginUser(email, password)

        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    Log.w("api-check ", "error ${response.errorBody()?:"null"}")
                    val errorBody = response.errorBody()?.string()
                    val messageResponse = Gson().fromJson(errorBody, MessageResponse::class.java)
                    val errorMessage = messageResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

    fun changePassword(email: String, currentPassword: String, newPassword: String, callback: (User?, String?)->Unit){
        val call = api.updatePassword(email, currentPassword, newPassword)
        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val messageResponse = Gson().fromJson(errorBody, MessageResponse::class.java)
                    val errorMessage = messageResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

    fun changeUsername(email: String, newUsername: String, password: String, callback: (User?, String?)-> Unit){
        val call = api.updateUsername(email, newUsername, password)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val messageResponse = Gson().fromJson(errorBody, MessageResponse::class.java)
                    val errorMessage = messageResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }
    fun changeProfilePicture(email: String, newImageUrl: String, callback: (User?, String?)->Unit){
        val call = api.updateProfilePic(email, newImageUrl)

        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val messageResponse = Gson().fromJson(errorBody, MessageResponse::class.java)
                    val errorMessage = messageResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }
    fun sendOtp(email: String, callback:(Boolean, String)->Unit){
        val call = api.sendOtp(
            hashMapOf(
                "email" to email
            )
        )
        call.enqueue(object: Callback<MessageResponse>{
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if(response.isSuccessful){
                    Log.w("api-check", "sendOtp: Success")
                    val res = response.body()
                    callback(true, res?.message?:"Couldn't get success message")
                }
                else{
                    Log.w("api-check", "sendOtp: NotSuccess")
                    val res = response.body()
                    callback(false, res?.message?:"Something went wrong")
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                callback(false, "Couldn't send OTP.")
            }

        })
    }

    fun verifyOtp(email: String, otp: String, callback: (Boolean, String)->Unit){
        val call = api.verifyOtp(
            hashMapOf(
                "email" to email,
                "userOtp" to otp
            )
        )
        call.enqueue(object: Callback<MessageResponse>{
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if(response.isSuccessful){
                    Log.w("api-check", "verifyOtp: Success")
                    val res = response.body()
                    callback(true, res?.message?:"Couldn't get success message")
                }
                else{
                    Log.w("api-check", "verifyOtp: NotSuccess")
                    val res = response.body()
                    callback(false, res?.message?:"Something went wrong")
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
               callback(false, "Something went wrong")
            }

        })
    }

    fun signup(name: String, email: String, password: String, image: String, callback: (User?, String?)-> Unit){
        val call = api.signupUser(name, email, password, image)

        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val messageResponse = Gson().fromJson(errorBody, MessageResponse::class.java)
                    val errorMessage = messageResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

    fun updateUser(
        email: String,
        password: String,
        image:String,
        username:String,
        callback: (User?, String?) -> Unit
    ){
        val call = api.updateUserDetails(
            email = email,
            password = password,
            image = image,
            username = username
        )
        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    val errorBody = response.errorBody()?.string()
                    val messageResponse = Gson().fromJson(errorBody, MessageResponse::class.java)
                    val errorMessage = messageResponse?.message?:"Something went wrong"
                    callback(null, errorMessage)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, t.message)
            }
        })
    }
}