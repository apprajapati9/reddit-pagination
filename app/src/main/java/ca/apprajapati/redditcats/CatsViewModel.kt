package ca.apprajapati.redditcats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import ca.apprajapati.redditcats.entities.AllCats
import ca.apprajapati.redditcats.entities.Resource
import ca.apprajapati.redditcats.getCatsUseCase.GetCatsUseCase
import ca.apprajapati.redditcats.network.CatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


data class CatPostState(
    val isLoading: Boolean = false,
    val cats: List<AllCats> = emptyList(),
    val error: String = ""
)


@HiltViewModel
class CatsViewModel @Inject constructor(
    repository: CatsRepository,
    val getCatsUseCase: GetCatsUseCase
) : ViewModel() {

    private val _cats = MutableStateFlow(CatPostState())
    val cats: StateFlow<CatPostState> get() = _cats

    val pagingCats = repository.getPagedCats().cachedIn(viewModelScope)

    fun getCats() {
        getCatsUseCase().onEach { result ->

            when (val state = result) {
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