package ca.apprajapati.redditcats.network

import ca.apprajapati.redditcats.entities.CatResponse
import ca.apprajapati.redditcats.network.CatsApi.Companion.BASE_URL
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/*
Without below property - it will throw an error
kotlinx.serialization.json.internal.JsonDecodingException: Encountered an unknown key 'after' at offset 30 at path: $.data
Use 'ignoreUnknownKeys = true' in 'Json {}' builder or '@JsonIgnoreUnknownKeys' annotation to ignore unknown keys.
 */
val json = Json {
    ignoreUnknownKeys = true
}

interface CatsApi {

    @GET(value = "r/catpics/hot.json")
    suspend fun getCatPics(
        @Query("limit") loadSize: Int = 0, //number of posts to return
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        //  @Query("count") count: Int = 10,
    ): Response<CatResponse>

    companion object {
        const val BASE_URL = "https://reddit.com/"
    }
}

object Retrofit {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF-8".toMediaType()))
            .build()
    }

    val catsApi: CatsApi = retrofit.create(CatsApi::class.java)
}

/*
    To use kotlinx serialization
    1. Use @Serializable annotation
    2. Use github.com/square/retrofit/tree/trunk/retrofit-converters/kotlinx-serialization for more info
    3. add depend converter - check this project.
 */