package ca.apprajapati.redditcats.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import ca.apprajapati.redditcats.entities.CatInfo
import ca.apprajapati.redditcats.entities.CatResponse
import kotlinx.coroutines.flow.Flow

interface CatsRepository {
    suspend fun getCats() : CatResponse
    fun getPagedCats() : Flow<PagingData<CatInfo>>
}


class CatsRepositoryImpl(private val api: CatsApi) : CatsRepository {
    override suspend fun getCats(): CatResponse {
        return api.getCatPics()
    }

    override fun getPagedCats() = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { RedditPagingSource(api)}
    ).flow
}