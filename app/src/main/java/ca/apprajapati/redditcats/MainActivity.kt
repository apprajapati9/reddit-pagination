package ca.apprajapati.redditcats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ca.apprajapati.redditcats.ui.theme.RedditCatsTheme
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade


sealed interface Screen {
    object Home : Screen
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                                    ShowImage()
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
fun ShowImage() {
    Column {

        Text(text = "Ajay")

        AsyncImage(
            modifier = Modifier.wrapContentSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://i.redd.it/pj7tlwrehvdf1.jpeg")
                .crossfade(true).build(),
            contentDescription = "Cat Image",
            contentScale = ContentScale.Crop,
        )

    }
}