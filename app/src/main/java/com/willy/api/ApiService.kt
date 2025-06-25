package com.willy.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("users")
    fun getUsers(): Call<UserResponse>

    @GET("products")
    fun getProducts(): Call<ProductResponse>
}

data class UserResponse(val users: List<User>)
