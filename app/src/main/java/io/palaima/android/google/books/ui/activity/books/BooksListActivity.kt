package io.palaima.android.google.books.ui.activity.books

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import com.miguelcatalan.materialsearchview.MaterialSearchView
import dagger.android.AndroidInjection
import io.palaima.android.google.books.R
import io.palaima.android.google.books.data.model.Book
import io.palaima.android.google.books.data.repository.BooksRepository
import io.palaima.android.google.books.ui.activity.book.BookDetailActivity
import io.palaima.android.google.books.ui.activity.books.adapter.BooksAdapter
import io.palaima.android.google.books.ui.activity.books.adapter.BooksDiffCallback
import io.palaima.android.google.books.ui.activity.books.adapter.EndlessRecyclerViewScrollListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.books_list_empty_state_content.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*
import timber.log.Timber
import javax.inject.Inject

class BooksListActivity : AppCompatActivity() {

  @Inject lateinit var booksRepository: BooksRepository

  private val books: MutableList<Book> = mutableListOf()
  private var booksSubscription: Disposable? = null
  private var scrollListener: EndlessRecyclerViewScrollListener? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_item_list)

    setSupportActionBar(toolbar)
    toolbar.title = title

    setupRecyclerView()
    setupSwipeToRefresh()
    setupSearchView()
    refreshBooks(books)
  }

  override fun onDestroy() {
    books_list.removeOnScrollListener(scrollListener)
    booksSubscription?.dispose()
    super.onDestroy()
  }

  override fun onBackPressed() {
    if (search_view.isSearchOpen) {
      search_view.closeSearch()
    } else {
      super.onBackPressed()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    val item = menu?.findItem(R.id.action_search)
    search_view.setMenuItem(item)
    return true
  }

  private fun setupRecyclerView() {
    val linearLayoutManager = LinearLayoutManager(this)
    books_list.layoutManager = linearLayoutManager

    books_list.adapter = BooksAdapter(books) { view, book ->
      val intent = Intent(this@BooksListActivity, BookDetailActivity::class.java).apply {
        putExtra(BookDetailActivity.ARG_BOOK_ID, book.id)
        putExtra(BookDetailActivity.ARG_BOOK_IMAGE_URL, book.thumbnail)
      }
      val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.book_image as View, "details")
      startActivity(intent, options.toBundle())
    }

    scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
      override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        refreshBooks(books, page)
      }
    }

    books_list.addOnScrollListener(scrollListener)
  }

  private fun setupSwipeToRefresh() {
    books_refresh.setColorSchemeResources(R.color.colorAccent)
    books_refresh.setOnRefreshListener {
      scrollListener?.resetState()
      refreshBooks(books, cached = false)
    }
  }

  private fun setupSearchView() {
    search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        refreshBooks(books, query = query.orEmpty(), cached = true)
        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
          refreshBooks(books, cached = true)
          return true
        }
        return false
      }
    })
    search_view.setSuggestions(resources.getStringArray(R.array.query_suggestions))
  }

  private fun bindBooks(diffResult: DiffUtil.DiffResult, newBooks: List<Book>) {
    books_content_switcher.displayedChildId = books_list.id
    books.clear()
    books.addAll(newBooks)
    diffResult.dispatchUpdatesTo(books_list.adapter)
  }

  private fun bindNoBooks() {
    books_content_switcher.displayedChildId = books_empty_state_container.id
    books.clear()
    scrollListener?.resetState()
    books_list.adapter.notifyDataSetChanged()
  }

  private fun refreshBooks(books: List<Book>, page: Int = 0, query: String = "fiction", cached: Boolean = false) {
    booksSubscription = booksRepository.findBooks(page, query, cached)
      .map {
        if (page == 0) {
          it
        } else {
          // Append newly fetched books
          val newBooks = books.toMutableList()
          newBooks.addAll(it)
          newBooks
        }
      }
      .map {
        val diff = DiffUtil.calculateDiff(BooksDiffCallback(books, it))
        Pair(it, diff)
      }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe { books_refresh.isRefreshing = true }
      .doFinally { books_refresh.isRefreshing = false }
      .subscribe({
        if (it.first.isNotEmpty()) {
          bindBooks(it.second, it.first)
        } else {
          bindNoBooks()
        }
      }, { e ->
        bindNoBooks()
        Timber.e(e, "Error while trying to fetch books")
      })
  }
}
