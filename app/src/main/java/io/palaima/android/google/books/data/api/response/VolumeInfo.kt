package io.palaima.android.google.books.data.api.response

data class VolumeInfo(
  val title: String,
  val authors: List<String>?,
  val publisher: String,
  val publishedDate: String,
  val description: String,
  val pageCount: Int,
  val printType: String,
  val categories: List<String>,
  val averageRating: Double,
  val ratingsCount: Int,
  val maturityRating: String,
  val allowAnonLogging: Boolean,
  val contentVersion: String,
  val imageLinks: ImageLinks,
  val language: String,
  val previewLink: String,
  val infoLink: String,
  val canonicalVolumeLink: String
)
