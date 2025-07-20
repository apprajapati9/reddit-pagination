package ca.apprajapati.redditcats.entities

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


@Serializable
data class CatResponse(
    val `data`: PostData
)

@Serializable
data class PostData(
    val children: List<AllCats>,
    val after: String?,
    val before: String?
)

@Serializable
data class AllCats(
    val `data`: CatInfo
)

@Serializable
data class CatInfo(
    val author: String,
    val url: String,
    val title : String,
)

sealed class Resource<out T: Any?> {
    data class Success<out T: Any?>(val data: T) : Resource<T>()
    data class Error<out T: Any?>(val data : T? = null, val message : String) : Resource<Nothing>()
    object Loading: Resource<Nothing>()
}