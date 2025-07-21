package ca.apprajapati.redditcats

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ca.apprajapati.redditcats.entities.AllCats
import ca.apprajapati.redditcats.entities.CatInfo
import ca.apprajapati.redditcats.getCatsUseCase.GetCatsUseCase
import ca.apprajapati.redditcats.network.CatsRepositoryImpl
import ca.apprajapati.redditcats.network.Retrofit
import ca.apprajapati.redditcats.ui.theme.RedditCatsTheme
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.collections.set


@Serializable
sealed interface ScreenKey : NavKey


@Serializable
sealed interface Screen : ScreenKey {
    @Serializable
    object WithoutPaging : Screen

    @Serializable
    object Paging : Screen
}


class MainActivity : ComponentActivity() {

    private lateinit var catsViewModel: CatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val repository = CatsRepositoryImpl(Retrofit.catsApi)
        val factory = ViewModelFactory(repository, GetCatsUseCase(repository))
        catsViewModel = ViewModelProvider(this, factory)[CatsViewModel::class.java]

        setContent {
            RedditCatsTheme {

                val tabs = listOf(Screen.WithoutPaging, Screen.Paging)

                val tabBackStacks = remember {
                    TabBackStacks<ScreenKey>(
                        startDestinations = tabs.associateWith { it }
                    )
                }

                var selectedTab by rememberSaveable(stateSaver = AppScreenKeySaver) {
                    mutableStateOf(Screen.WithoutPaging)
                }
                val backStack = tabBackStacks.getStack(selectedTab)

                val pagedCats = catsViewModel.pagingCats.collectAsLazyPagingItems()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            tabs.forEach { tab ->
                                val label = when (tab) {
                                    Screen.Paging -> {
                                        "Paging"
                                    }

                                    Screen.WithoutPaging -> {
                                        "Without Paging"
                                    }
                                }

                                val icon = when (tab) {
                                    Screen.Paging -> Icons.Default.Home
                                    Screen.WithoutPaging -> Icons.Default.Person
                                }

                                BottomBarItem(
                                    label = label,
                                    icon = icon,
                                    selected = selectedTab == tab
                                ) {
                                    selectedTab = tab
                                }
                            }

                        }
                    }) { innerPadding ->

                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        NavDisplay(
                            entryDecorators = listOf(
//                                rememberSavedStateNavEntryDecorator(),
//                                rememberSceneSetupNavEntryDecorator()
                            ),
                            backStack = backStack,
                            onBack = { steps ->
                                repeat(steps) {
                                    tabBackStacks.pop(selectedTab)
                                }
                            },
                            entryProvider = entryProvider {

                                entry<Screen.WithoutPaging> {
                                    ShowImage(catsViewModel)
                                }

                                entry<Screen.Paging> {
                                    PagedCatsScreen(pagedCats)
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
fun PagedCatsScreen(cats: LazyPagingItems<CatInfo>) {

    val context = LocalContext.current

    LaunchedEffect(key1 = cats.loadState) {
        if (cats.loadState.refresh is LoadState.Error) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    val favorites =  remember {
        mutableStateMapOf<String, Boolean>()
    }

    LaunchedEffect(favorites.size) {
        favorites.forEach {
            k,v ->
                Log.d("Ajay", "id -> $k, v -> $v added")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (cats.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                items(count = cats.itemCount, key = {
                    cats[it]?.imageUrl ?: -1
                }) { index ->
                    cats[index]?.let {
                        ShowCat(it, favorites.contains(it.id)) {
                            id ->
                            if(favorites.contains(id)){
                                favorites.remove(id)
                            }else{
                               favorites[id] = true
                            }
                        }
                    }
                }

                item {
                    if (cats.loadState.append is LoadState.Loading) {
                        CircularProgressIndicator()
                    }
                }

            }
        }
    }
}


@Composable
fun ShowImage(viewmodel: CatsViewModel) {

    val cats by viewmodel.cats.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        Log.d("Ajay", "LaunchedEffect -> Api call to get Cats")
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

    if (cats.error.isNotEmpty()) {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = cats.error
        )
    }


}

@Composable
fun ShowImages(cats: List<AllCats>) {

    LazyColumn {
        items(
            items = cats
        ) { cat ->
//            ShowCat(cat.data) {
//
//            }
        }
    }
}


@Composable
fun ShowCat(cat: CatInfo, isFav: Boolean, addToFav: (String) -> Unit) {
    Card(
        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 1.dp, end = 1.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Spacer(Modifier.height(12.dp))
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(if (cat.imageUrl.contains("gallery")) cat.thumbnail else cat.imageUrl)
                    .crossfade(true).build(),
                contentDescription = "Cat Image",
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.3f))
                .height(50.dp)
                .fillMaxWidth())

            Text(modifier = Modifier.align(Alignment.BottomCenter), text = cat.title, color = Color.White, maxLines = 2)

            IconButton(modifier = Modifier.align(Alignment.TopEnd), onClick = {
                addToFav(cat.id)
            }) {
                Icon(imageVector = if(isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = if(isFav) Color.Red else Color.Black)
            }
        }

    }
}

// Tabs stack logic and stack impl
val AppScreenKeySaver = Saver<ScreenKey, String>(
    save = { Json.encodeToString(ScreenKey.serializer(), it) },
    restore = { Json.decodeFromString(ScreenKey.serializer(), it) }
)

/*
    Data organization format
    Home [Key] -> V ... list
    Profile[key] -> ProfileDetails.. list
    Settings[Key] -> SettingsDetails... list.
 */
class TabBackStacks<T : ScreenKey>(val startDestinations: Map<T, T>) {
    private val stacks = mutableStateMapOf<T, SnapshotStateList<T>>()

    init {
        startDestinations.forEach { (tab, root) ->
            stacks[tab] = mutableStateListOf(root)
        }
    }

    fun getStack(tab: T): SnapshotStateList<T> = stacks[tab]!!

    fun push(tab: T, screen: T) {
        stacks[tab]?.add(screen)
    }

    fun pop(tab: T) {
        stacks[tab]?.removeLastOrNull()
    }

    fun current(tab: T): T = stacks[tab]?.last() ?: startDestinations[tab]!!
}

@Composable
fun RowScope.BottomBarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) }
    )
}