package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.network.jsonadapter.DateAdapter
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object CivicsApiInstance {

    private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"
    private const val API_KEY = BuildConfig.API_KEY

    //  Add adapters for Java Date and custom adapter ElectionAdapter (included in project)
    private val moshi = Moshi.Builder()
        .add(ElectionAdapter())
        .add(DateAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(buildOkHttpClient())
        .baseUrl(BASE_URL)
        .build()

    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val url = original
                .url()
                .newBuilder()
                .addQueryParameter("key", API_KEY)
                .build()
            val request = original
                .newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }.build()
    }

    private val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }

    suspend fun getElections() = retrofitService.getElections()

    suspend fun getVoterInfo(address: String, id: Int) = retrofitService.getVoterInfo(address, id)

    suspend fun getAllRepresentatives(address: String) = retrofitService.getAllRepresentatives(address)

}

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {
    // Add elections API Call
    @GET("elections")
    suspend fun getElections(): ElectionResponse

    // Add voterinfo API Call
    @GET("voterinfo")
    suspend fun getVoterInfo(
        @Query("address") address: String,
        @Query("electionId") electionId: Int
    ): VoterInfoResponse

    // Add representatives API Call
    @GET("representatives")
    fun getAllRepresentatives(@Query("address") address: String): RepresentativeResponse
}

object CivicsApi {

}