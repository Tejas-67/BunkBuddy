package com.tejasdev.bunkbuddy.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T, K, R> LiveData<T>.combine(
    liveData: LiveData<K>,
    callback: (T?, K?) -> R
): LiveData<R>{
    val result = MediatorLiveData<R>()
    result.addSource(this){
        result.value = callback(this.value, liveData.value)
    }
    result.addSource(liveData){
        result.value = callback(this.value, liveData.value)
    }
    return result
}