package com.tejasdev.bunkbuddy.api

import com.tejasdev.bunkbuddy.datamodel.DataUploadPacket
import com.tejasdev.bunkbuddy.datamodel.DownloadData
import com.tejasdev.bunkbuddy.datamodel.MessageResponse
import com.tejasdev.bunkbuddy.datamodel.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthAPI {
    @GET("/login")
    fun loginUser(
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<User>


    @GET("/signup")
    fun signupUser(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("image") image: String
    ): Call<User>

    @POST("/update-profile-picture")
    fun updateProfilePic(
        @Query("email") email: String,
        @Query("imageUrl") imageUrl: String
    ): Call<User>

    @GET("/update-password")
    fun updatePassword(
        @Query("email") email: String,
        @Query("currentPassword") currPassword: String,
        @Query("newPassword") newPassword: String
    ): Call<User>

    @GET("/update")
    fun updateUserDetails(
        @Query("password") password: String,
        @Query("image") image: String,
        @Query("email") email: String,
        @Query("username") username: String
    ): Call<User>

    @POST("/update-username")
    fun updateUsername(
        @Query("email") email: String,
        @Query("newUsername") newUsername: String,
        @Query("password") password: String
    ): Call<User>

    @POST("/otp/send-otp")
    fun sendOtp(
        @Body map: HashMap<String, String>
    ): Call<MessageResponse>

    @POST("/otp/verify-otp")
    fun verifyOtp(
        @Body map: HashMap<String, String>
    ): Call<MessageResponse>

    @POST("/backup/upload")
    fun backupData(
        @Body packet: DataUploadPacket
    ): Call<MessageResponse>

    @POST("/backup/fetch")
    fun fetchBackedUpData(
        @Body map: HashMap<String, String>
    ): Call<DownloadData>
}