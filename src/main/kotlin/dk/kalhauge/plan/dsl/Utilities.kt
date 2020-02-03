package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Address
import dk.kalhauge.util.of

fun String.last(count: Int) = this.substring(this.length - count)

fun Int.zeroize(count: Int = 2): String {
  val t = this.toString()
  return "${count - t.length of "0"}$t"
  }

val String.web get() = Address.Web(this)
val String.file get() = Address.Disk(this)