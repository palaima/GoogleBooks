package io.palaima.android.google.books.di

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.palaima.android.google.books.BuildConfig
import io.palaima.android.google.books.data.api.BooksApi
import io.palaima.android.google.books.data.api.interceptor.ApiKeyInterceptor
import io.palaima.android.google.books.data.repository.BooksRepository
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

  @Provides
  @Singleton
  fun provideContext(application: Application): Context = application

  @Provides
  @Singleton
  fun provideGson(): Gson = GsonBuilder().create()

  @Provides
  @Singleton
  fun provideCache(context: Context) = Cache(context.cacheDir, CACHE_SIZE)

  @Provides
  @Singleton
  fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    return loggingInterceptor
  }

  @Provides
  @Singleton
  fun provideApiKeyInterceptor() = ApiKeyInterceptor(BuildConfig.API_KEY)

  @Provides
  @Singleton
  fun provideOkHttpClient(cache: Cache, apiKeyInterceptor: ApiKeyInterceptor, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
      .cache(cache)
      .addInterceptor(apiKeyInterceptor)
      .addInterceptor(loggingInterceptor)
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .baseUrl(BASE_URL)
      .client(client)
      .build()
  }

  @Provides
  @Singleton
  fun provideBooksApi(retrofit: Retrofit): BooksApi = retrofit.create(BooksApi::class.java)

  @Provides
  @Singleton
  fun provideBooksRepository(api: BooksApi) = BooksRepository(api)

  companion object {
    val BASE_URL = "https://www.googleapis.com/"
    val CACHE_SIZE: Long = 10 * 1024 * 1024
  }
}
