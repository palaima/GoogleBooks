package io.palaima.android.google.books.ui.activity.book

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import io.palaima.android.google.books.R
import io.palaima.android.google.books.data.model.Book
import io.palaima.android.google.books.data.repository.BooksRepository
import io.palaima.android.google.books.ui.activity.books.BooksListActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_item_detail.*
import timber.log.Timber
import javax.inject.Inject

class BookDetailActivity : AppCompatActivity() {

  @Inject lateinit var booksRepository: BooksRepository

  private var loadSubscription: Disposable? = null
  private var book: Book? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_item_detail)
    setSupportActionBar(detail_toolbar)

    fab.setOnClickListener { view ->
      Snackbar.make(view, "Book saved", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show()
    }

    // Show the Up button in the action bar.
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    postponeEnterTransition()

    val imageUrl = intent.getStringExtra(ARG_BOOK_IMAGE_URL)
    Picasso.with(this)
      .load(imageUrl)
      .into(object : com.squareup.picasso.Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
          scheduleStartPostponedTransition(book_image)
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
          scheduleStartPostponedTransition(book_image)
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
          bitmap?.let {
            book_image.setImageBitmap(it)
          }
          // Call the "scheduleStartPostponedTransition()" method
          // below when you know for certain that the shared element is
          // ready for the transition to begin.
          scheduleStartPostponedTransition(book_image)
        }
      })

    val bookId = intent.getStringExtra(ARG_BOOK_ID)

    // Load cached if we have one
    loadSubscription = booksRepository.getBookById(bookId, cached = true)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        this.book = it
        bindBook(it)
      }, {
        Timber.e(it, "Error while trying to load book")
      })
  }

  override fun onDestroy() {
    loadSubscription?.dispose()
    super.onDestroy()
  }

  override fun onOptionsItemSelected(item: MenuItem) =
    when (item.itemId) {
      android.R.id.home -> {
        supportFinishAfterTransition()
        navigateUpTo(Intent(this, BooksListActivity::class.java))
        true
      }
      else -> super.onOptionsItemSelected(item)
    }

  private fun bindBook(book: Book) {
    toolbar_layout?.title = book.title
    item_detail.text = book.description
    // Load high quality image
    Picasso.with(this)
      .load(book.imageUrl)
      .placeholder(book_image.drawable)
      .error(book_image.drawable)
      .into(book_image)
  }

  /**
   * Schedules the shared element transition to be started immediately
   * after the shared element has been measured and laid out within the
   * activity's view hierarchy. Some common places where it might make
   * sense to call this method are:
   *
   * (1) Inside a Fragment's onCreateView() method (if the shared element
   * lives inside a Fragment hosted by the called Activity).
   *
   * (2) Inside a Picasso Callback object (if you need to wait for Picasso to
   * asynchronously load/scale a bitmap before the transition can begin).
   */
  private fun scheduleStartPostponedTransition(sharedElement: View) {
    sharedElement.viewTreeObserver.addOnPreDrawListener(
      object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
          sharedElement.viewTreeObserver.removeOnPreDrawListener(this)
          startPostponedEnterTransition()
          return true
        }
      })
  }

  companion object {
    val ARG_BOOK_ID = "book_id"
    val ARG_BOOK_IMAGE_URL = "book_image_url"
  }
}
