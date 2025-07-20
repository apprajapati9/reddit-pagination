package ca.apprajapati.redditcats.getCatsUseCase

import android.util.Log
import ca.apprajapati.redditcats.entities.AllCats
import ca.apprajapati.redditcats.entities.Resource
import ca.apprajapati.redditcats.network.CatsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.internal.connection.Exchange

class GetCatsUseCase(val repository: CatsRepository) {

    operator fun invoke() : Flow<Resource<List<AllCats>>> = flow {
        emit(Resource.Loading)
        delay(1000)

        try {
            val response = repository.getCats()

            val allCatsPosts = response.data.children

            Log.d("Ajay", "All cats $allCatsPosts")

            emit(Resource.Success(allCatsPosts))
        }catch(e: Exception){
            e.printStackTrace()
            emit(Resource.Error(data = null, message = "Something went wrong! Try again ${e.printStackTrace()}"))
        }

    }
}