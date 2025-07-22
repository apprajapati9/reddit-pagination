package ca.apprajapati.redditcats.getCatsUseCase

import android.util.Log
import ca.apprajapati.redditcats.entities.AllCats
import ca.apprajapati.redditcats.entities.Resource
import ca.apprajapati.redditcats.network.CatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCatsUseCase @Inject constructor(val repository: CatsRepository) {

    //when we define a function named invoke(), it allows instances of a class to be called as if they were functions, do it only if you want to call instance of a class to behave like a function. other use cases are for operator overloading
    operator fun invoke() : Flow<Resource<List<AllCats>>> = flow {
        emit(Resource.Loading)

        try {
            val response = repository.getCats()

            val allCatsPosts = response.data.children

            emit(Resource.Success(allCatsPosts))
        }catch(e: Exception){
            e.printStackTrace()
            emit(Resource.Error(data = null, message = "Something went wrong! Try again ${e.printStackTrace()}"))
        }

    }
}