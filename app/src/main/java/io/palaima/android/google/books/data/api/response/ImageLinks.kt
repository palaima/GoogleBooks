package io.palaima.android.google.books.data.api.response

data class ImageLinks(
  val smallThumbnail: String,
  val thumbnail: String,
  private val small: String?,
  private val medium: String?,
  private val large: String?
) {
  val defaultUrl: String
    get() =
      when {
        large != null -> large
        medium != null -> medium
        small != null -> small
        else -> thumbnail
      }
}
