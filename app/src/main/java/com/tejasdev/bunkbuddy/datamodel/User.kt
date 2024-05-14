package com.tejasdev.bunkbuddy.datamodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("__v") var v: Int = 0,
    @SerializedName("_id") var id: String = "",
    var createdAt: String = "",
    val email: String,
    val name: String,
    val password: String,
    var updatedAt: String = "",
    val image: String,
    var isVerified: Boolean = false
): Parcelable