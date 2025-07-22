package ca.apprajapati.redditcats.network

import ca.apprajapati.redditcats.entities.CatResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

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

/*
    To use kotlinx serialization
    1. Use @Serializable annotation
    2. Use github.com/square/retrofit/tree/trunk/retrofit-converters/kotlinx-serialization for more info
    3. add depend converter - check this project.
 */