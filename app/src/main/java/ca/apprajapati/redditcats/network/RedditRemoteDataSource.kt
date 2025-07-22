package ca.apprajapati.redditcats.network

import android.util.Log
import ca.apprajapati.redditcats.entities.CatResponse
import retrofit2.Response
import javax.inject.Inject

interface RedditRemoteDataSource {
    suspend fun getCats(loadSize: Int = 0, before: String? = null, after: String? = null) : Response<CatResponse>
}


class RedditRemoteDataSourceImpl @Inject constructor(val api: CatsApi) : RedditRemoteDataSource {
    override suspend fun getCats(
        loadSize: Int,
        before : String?,
        after: String?
    ): Response<CatResponse> {
        //Log.d("Ajay", "Passed $loadSize, before $before, after $after")
        return api.getCatPics(loadSize, after, before)
    }

}