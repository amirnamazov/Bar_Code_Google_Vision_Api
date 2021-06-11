package com.example.barcode.data.api

import com.example.barcode.data.model.Post
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface SimpleApi {
    @POST("/posts")
    fun postCode(@Body post: Post): Call<Post>
}