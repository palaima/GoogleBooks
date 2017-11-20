package io.palaima.android.google.books.ui.activity.books.adapter

import android.support.v7.util.DiffUtil
import io.palaima.android.google.books.data.model.Book

class BooksDiffCallback(private val old: List<Book>, private val new: List<Book>) : DiffUtil.Callback() {

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    val o = old[oldItemPosition]
    val n = new[newItemPosition]

    if (o.id == n.id) {
      return true
    }

    return o == n
  }

  override fun getOldListSize(): Int {
    return old.size
  }

  override fun getNewListSize(): Int {
    return new.size
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    val o = old[oldItemPosition]
    val n = new[newItemPosition]

    return o == n
  }
}