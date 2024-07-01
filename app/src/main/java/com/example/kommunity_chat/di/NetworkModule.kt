package com.example.kommunity_chat.di

import com.example.kommunity_chat.utils.AuthInterceptor
import com.example.kommunity_chat.api.ChatBotAPI
import com.example.kommunity_chat.utils.LoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule{
    @Provides
    @Singleton
    fun provideRetrofitChatBotApiApi(client: OkHttpClient):ChatBotAPI{
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl( "https://whb98bdc03e9f3255d88.free.beeceptor.com")
            .client(client)
            .build()
        return retrofit.create(ChatBotAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpClient():OkHttpClient{
        return  OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor)
            .addInterceptor(AuthInterceptor)
            .build()
    }

}