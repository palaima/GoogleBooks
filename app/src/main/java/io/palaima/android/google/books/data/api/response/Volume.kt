package io.palaima.android.google.books.data.api.response

data class Volume(
  val kind: String,
  val id: String,
  val etag: String,
  val selfLink: String,
  val volumeInfo: VolumeInfo
)
