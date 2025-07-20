package ca.apprajapati.redditcats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ca.apprajapati.redditcats.entities.AllCats
import ca.apprajapati.redditcats.entities.CatInfo
import ca.apprajapati.redditcats.getCatsUseCase.GetCatsUseCase
import ca.apprajapati.redditcats.network.CatsRepositoryImpl
import ca.apprajapati.redditcats.network.Retrofit
import ca.apprajapati.redditcats.ui.theme.RedditCatsTheme
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade


sealed interface Screen {
    object Home : Screen
}


class MainActivity : ComponentActivity() {

    private lateinit var catsViewModel : CatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val repository = CatsRepositoryImpl(Retrofit.catsApi)
        val factory = ViewModelFactory(GetCatsUseCase(repository))
        catsViewModel = ViewModelProvider(this, factory).get(CatsViewModel::class.java)

        setContent {
            RedditCatsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val backStack = mutableListOf<Screen>(Screen.Home)

                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        NavDisplay(
                            backStack = backStack,
                            onBack = { backStack.removeFirstOrNull() },
                            entryProvider = entryProvider {

                                entry<Screen.Home> {
                                    ShowImage(catsViewModel)
                                }
                            }
                        )

                    }
                }
            }
        }
    }
}


@Composable
fun ShowImage(viewmodel : CatsViewModel) {

    val cats by viewmodel.cats.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewmodel.getCats()
    }

    AnimatedVisibility(
        visible = !cats.isLoading
    ) {
        ShowImages(cats.cats)
    }

    AnimatedVisibility(
        visible = cats.isLoading
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
    }


}

@Composable
fun ShowImages(cats: List<AllCats>){

    LazyColumn {
        items(
            items = cats
        ) {
            cat ->
                ShowCat(cat.data)
        }
    }
}


@Composable
fun ShowCat(cat : CatInfo){
    Card {
        Text(text = cat.title)
        AsyncImage(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(cat.url)
                .crossfade(true).build(),
            contentDescription = "Cat Image",
            contentScale = ContentScale.Fit,
        )

    }
}