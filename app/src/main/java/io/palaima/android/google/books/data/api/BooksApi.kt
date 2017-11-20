package io.palaima.android.google.books.data.api

import io.palaima.android.google.books.data.api.response.Volume
import io.palaima.android.google.books.data.api.response.Volumes
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksApi {

  @GET("/books/v1/volumes")
  fun getBooks(
    @Query("startIndex") startIndex: Int,
    @Query("maxResults") maxResults: Int,
    @Query("q") query: String,
    @Query("orderBy") orderBy: String
  ): Single<Response<Volumes>>

  @GET("/books/v1/volumes/{id}")
  fun getBookById(
    @Path("id") id: String,
    @Query("projection") projection: String
  ): Single<Volume>
}