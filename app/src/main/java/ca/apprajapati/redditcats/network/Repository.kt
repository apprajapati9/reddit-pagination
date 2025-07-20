package ca.apprajapati.redditcats.network

import ca.apprajapati.redditcats.entities.CatResponse

interface CatsRepository {
    suspend fun getCats() : CatResponse
}


class CatsRepositoryImpl(private val api: CatsApi) : CatsRepository {
    override suspend fun getCats(): CatResponse {
        return api.getCatPics()
    }
}