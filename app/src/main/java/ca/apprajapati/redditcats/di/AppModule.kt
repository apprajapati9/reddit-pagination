package ca.apprajapati.redditcats.di

import ca.apprajapati.redditcats.network.CatsApi
import ca.apprajapati.redditcats.network.CatsApi.Companion.BASE_URL
import ca.apprajapati.redditcats.network.CatsRepository
import ca.apprajapati.redditcats.network.CatsRepositoryImpl
import ca.apprajapati.redditcats.network.RedditRemoteDataSource
import ca.apprajapati.redditcats.network.RedditRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHTTPClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient().newBuilder().addInterceptor(interceptor).build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(): CatsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json; charset=UTF-8".toMediaType()))
        .build().create(CatsApi::class.java)


    @Provides
    @Singleton
    fun providesRemoteDataSource(catsApi: CatsApi): RedditRemoteDataSource =
        RedditRemoteDataSourceImpl(catsApi)

    @Provides
    @Singleton
    fun provideRepository(remoteDataSource: RedditRemoteDataSource): CatsRepository =
        CatsRepositoryImpl(remoteDataSource)


    /*
Without below property - it will throw an error
kotlinx.serialization.json.internal.JsonDecodingException: Encountered an unknown key 'after' at offset 30 at path: $.data
Use 'ignoreUnknownKeys = true' in 'Json {}' builder or '@JsonIgnoreUnknownKeys' annotation to ignore unknown keys.
*/
    val json = Json {
        ignoreUnknownKeys = true
    }

}


