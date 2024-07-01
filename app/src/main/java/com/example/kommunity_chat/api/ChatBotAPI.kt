package com.example.kommunity_chat.api

import com.example.kommunity_chat.data.model.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatBotAPI {

    @POST("response")
    suspend fun getMessage(@Query("message") message: String): Response<ResponseBody>

    @GET("response?message=Refundstatus")
    suspend fun  getRefundUpdate():Response<ResponseBody>

}