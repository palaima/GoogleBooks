package io.palaima.android.google.books.ui.activity.books.adapter

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.palaima.android.google.books.R
import io.palaima.android.google.books.data.model.Book
import kotlinx.android.synthetic.main.item_list_content.view.*

class BooksAdapter(
  private val books: List<Book>,
  private val action: (View, Book) -> Unit
) :
  RecyclerView.Adapter<BooksAdapter.ViewHolder>() {

  private val onClickListener: View.OnClickListener

  init {
    onClickListener = View.OnClickListener { v ->
      val item = v.tag as Book
      action(v, item)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_list_content, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = books[position]
    holder.title.text = item.title
    holder.author.text = item.author ?: "No author"

    Picasso.with(holder.itemView.context)
      .load(item.thumbnail)
      .into(holder.image)

    with(holder.itemView) {
      tag = item
      setOnClickListener(onClickListener)
    }
  }

  override fun getItemCount(): Int {
    return books.size
  }

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image: AppCompatImageView = view.book_image
    val title: TextView = view.book_title
    val author: TextView = view.book_author
  }
}