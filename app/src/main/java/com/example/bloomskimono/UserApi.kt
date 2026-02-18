package com.example.bloomskimono

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface UserApi {
    @GET("/users/test")
    fun testConnection(): Call<ResponseBody>
}