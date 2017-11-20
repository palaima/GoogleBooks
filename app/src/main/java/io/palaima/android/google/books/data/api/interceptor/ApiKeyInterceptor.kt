package io.palaima.android.google.books.data.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ApiKeyInterceptor(private val apiKey: String) : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    var request = chain.request()
    val url = request.url().newBuilder()
      .addQueryParameter("key", apiKey)
      .build()
    request = request.newBuilder().url(url).build()
    return chain.proceed(request)
  }
}
