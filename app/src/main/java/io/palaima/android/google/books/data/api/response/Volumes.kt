package io.palaima.android.google.books.data.api.response

data class Volumes(
  val kind: String,
  val totalItems: Int,
  val items: List<Volume>
)
