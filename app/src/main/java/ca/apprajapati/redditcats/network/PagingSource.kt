package ca.apprajapati.redditcats.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ca.apprajapati.redditcats.entities.AllCats
import ca.apprajapati.redditcats.entities.CatInfo
import coil3.network.HttpException
import okio.IOException

class RedditPagingSource(
    private val api: CatsApi
) : PagingSource<String, CatInfo>() {

    override fun getRefreshKey(state: PagingState<String, CatInfo>): String? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, CatInfo> {
        return try {

            val response = api.getCatPics(loadSize = params.loadSize)

            val listing = response.data //Reddit listing api name

            val redditCatPosts = listing.children.map { it.data }

            LoadResult.Page(
                data = redditCatPosts,
                prevKey = listing.before,
                nextKey = listing.after
            )
        }catch (e: IOException) {
            return LoadResult.Error(e)
        }catch (e: HttpException){
            return LoadResult.Error(e)
        }
    }

}