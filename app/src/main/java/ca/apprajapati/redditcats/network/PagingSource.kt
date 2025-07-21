package ca.apprajapati.redditcats.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ca.apprajapati.redditcats.entities.AllCats
import ca.apprajapati.redditcats.entities.CatInfo
import coil3.network.HttpException
import okio.IOException


class RedditPagingSource(
    private val api: CatsApi
) : PagingSource<String, CatInfo>() {


    override val keyReuseSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<String, CatInfo>): String? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, CatInfo> {
        val prevKey = params.key //stores next key that was set in Page(nextKey=) else null

        return try {

            val response = api.getCatPics(loadSize = params.loadSize, after = prevKey)

            val listing = response.body()?.data //Reddit listing api name

            val redditCatPosts = listing?.children?.map { it.data }


            Log.d("Ajay", "Before $prevKey, after $prevKey")
            LoadResult.Page(
                data = redditCatPosts ?: listOf(),
                prevKey = listing?.before,
                nextKey = listing?.after //this will become the Params.key in the next load.
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

}