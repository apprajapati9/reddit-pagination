package ca.apprajapati.redditcats.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
    val id: String,
    val author: String,
    @SerialName("url")
    val imageUrl: String,
    val title: String,
    val thumbnail: String
)

sealed class Resource<out T : Any?> {
    data class Success<out T : Any?>(val data: T) : Resource<T>()
    data class Error<out T : Any?>(val data: T? = null, val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}