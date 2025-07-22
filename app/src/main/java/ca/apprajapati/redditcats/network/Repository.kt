package ca.apprajapati.redditcats.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import ca.apprajapati.redditcats.entities.CatInfo
import ca.apprajapati.redditcats.entities.CatResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CatsRepository {
    suspend fun getCats(): CatResponse
    fun getPagedCats(): Flow<PagingData<CatInfo>>
}


class CatsRepositoryImpl @Inject constructor(private val remoteDataSource: RedditRemoteDataSource) : CatsRepository {
    override suspend fun getCats(): CatResponse {
        return remoteDataSource.getCats().body()!!
    }

    override fun getPagedCats() = Pager(
        config = PagingConfig(pageSize = 40, enablePlaceholders = false, prefetchDistance = 3),
        pagingSourceFactory = { RedditPagingSource(remoteDataSource) }
    ).flow
}