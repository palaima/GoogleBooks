package io.palaima.android.google.books.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.palaima.android.google.books.ui.activity.book.BookDetailActivity
import io.palaima.android.google.books.ui.activity.book.BookDetailsActivityModule
import io.palaima.android.google.books.ui.activity.books.BooksActivityModule
import io.palaima.android.google.books.ui.activity.books.BooksListActivity

@Module
abstract class ActivityBuilderModule {
  @ContributesAndroidInjector(modules = arrayOf(BooksActivityModule::class))
  internal abstract fun bindBooksActivity(): BooksListActivity

  @ContributesAndroidInjector(modules = arrayOf(BookDetailsActivityModule::class))
  internal abstract fun bindBookDetailActivity(): BookDetailActivity
}
