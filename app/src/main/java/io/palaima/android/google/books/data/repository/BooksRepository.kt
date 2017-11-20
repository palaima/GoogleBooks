package io.palaima.android.google.books.data.repository

import io.palaima.android.google.books.data.api.BooksApi
import io.palaima.android.google.books.data.model.Book
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber

class BooksRepository(private val api: BooksApi) {

  private val cachedBooks = mutableListOf<Book>()
  private var lastQuery: String = ""

  fun findBooks(page: Int, query: String, cached: Boolean = false): Observable<List<Book>> {
    // Return cached if possible
    if (cached && lastQuery == query && query.isNotEmpty() && cachedBooks.isNotEmpty()) {
      return Observable.just(cachedBooks)
    }
    return api.getBooks(page * MAX_RESULTS, MAX_RESULTS, query, ORDER_BY)
      .map { it.body()?.items }
      .flatMap {
        Observable.fromIterable(it)
          .map {
            // map volumes to books
            Timber.d("volume $it")
            Book(
              it.id,
              it.volumeInfo.title,
              it.volumeInfo.authors?.firstOrNull(),
              it.volumeInfo.description,
              it.volumeInfo.imageLinks.thumbnail,
              it.volumeInfo.imageLinks.defaultUrl
            )
          }
          .toList()
      }
      .toObservable()
      .doOnNext {
        // cache latest books
        lastQuery = query
        cachedBooks.clear()
        cachedBooks.addAll(it)
      }
  }

  fun getBookById(id: String, cached: Boolean = false): Single<Book> {
    // Return cached if possible
    if (cached) {
      val cachedBook = cachedBooks.firstOrNull { it.id == id }
      if (cachedBook != null) {
        return Single.just(cachedBook)
      }
    }
    return api.getBookById(id, PROJECTION)
      .map {
        Book(
          it.id,
          it.volumeInfo.title,
          it.volumeInfo.authors?.firstOrNull(),
          it.volumeInfo.description,
          it.volumeInfo.imageLinks.thumbnail,
          it.volumeInfo.imageLinks.defaultUrl
        )
      }
  }

  companion object {
    val MAX_RESULTS = 20
    val ORDER_BY = "newest"
    val PROJECTION = "full"
  }
}