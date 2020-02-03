package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Address
import dk.kalhauge.document.dsl.text
import javax.xml.soap.Text

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
  }

fun Course.book(
    title: String,
    label: String,
    subtitle: String? = null,
    isbn: String? = null,
    edition: String? = null,
    editor: String? = null,
    url: Address.Web? = null,
    authors: String? = null
    ) =
  Book(this, title, label, subtitle, isbn, edition, editor, url, authors).also {
    add(it)
    }