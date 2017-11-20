package io.palaima.android.google.books.data.model

data class Book(
  val id: String,
  val title: String,
  val author: String?,
  val description: String,
  val thumbnail: String,
  val imageUrl: String
)
