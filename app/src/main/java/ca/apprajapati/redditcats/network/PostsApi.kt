package ca.apprajapati.redditcats.network

import ca.apprajapati.redditcats.entities.CatResponse
import ca.apprajapati.redditcats.network.CatsApi.Companion.BASE_URL
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

val json = Json {
    ignoreUnknownKeys = true
}

interface CatsApi {

    @GET(value = "r/catpics/top.json")
    suspend fun getCatPics(
        @Query("after") after: String = "",
        @Query("limit") limit: String = "",
    ): CatResponse

    companion object {
        const val BASE_URL = "https://reddit.com/"
    }
}

object Retrofit {

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF-8".toMediaType()))
            .build()
    }

    val catsApi = retrofit.create(CatsApi::class.java)
}