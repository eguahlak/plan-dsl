package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Address

class Book(
    val course: Course,
    val title: String,
    val label: String,
    val subtitle: String?,
    val isbn: String?,
    val edition: String?,
    val editor: String?,
    val url: Address.Web?,
    val authors: String?
    ) {
  var optional = false
  var note: String? = null
  }

fun Course.book(
    title: String,
    label: String,
    subtitle: String? = null,
    isbn: String? = null,
    edition: String? = null,
    editor: String? = null,
    url: Address.Web? = null,
    authors: String? = null,
    build: Book.() -> Unit = {}
    ) =
  Book(this, title, label, subtitle, isbn, edition, editor, url, authors).also {
    it.build()
    add(it)
    }