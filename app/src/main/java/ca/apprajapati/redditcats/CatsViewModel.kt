package ca.apprajapati.redditcats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.apprajapati.redditcats.entities.AllCats
import ca.apprajapati.redditcats.entities.Resource
import ca.apprajapati.redditcats.getCatsUseCase.GetCatsUseCase
import ca.apprajapati.redditcats.network.CatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


data class CatPostState(
    val isLoading : Boolean = false,
    val cats: List<AllCats> = emptyList(),
    val error : String = ""
)


class CatsViewModel(
    val getCatsUseCase: GetCatsUseCase
) : ViewModel() {

    private val _cats = MutableStateFlow(CatPostState())
    val cats : StateFlow<CatPostState> get() = _cats

    fun getCats() {
        getCatsUseCase().onEach {
            result ->

                when(val state = result){
                    is Resource.Error<*> -> {
                        Log.d("Ajay", "Error...${state.message}")
                        _cats.value = CatPostState(error = state.message)
                    }
                    is Resource.Loading -> {
                        Log.d("Ajay", "Loading...")
                        _cats.value = CatPostState(isLoading = true)
                    }
                    is Resource.Success<*> -> {
                        Log.d("Ajay", "Success...${state.data}")
                        _cats.value = CatPostState(cats = state.data as List<AllCats>)
                    }
                }
        }.launchIn(viewModelScope)
    }

}

class ViewModelFactory(private val useCase : GetCatsUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CatsViewModel::class.java)){
            return CatsViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}