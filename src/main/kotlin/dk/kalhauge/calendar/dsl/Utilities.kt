package dk.kalhauge.calendar.dsl

import dk.kalhauge.util.toMD5

fun String.chuncked(count: Int): List<String> {
    val bytes = Charsets.UTF_8.encode(this)
    val size = bytes.limit()
    val list = mutableListOf<String>()
    var start = 0
    while (start < size) {
        var index = (start + count).coerceAtMost(size)
        bytes.limit(size)
        while (bytes[index - 1].toInt() and 0x80 != 0)
            index--
        bytes.limit(index)
        list.add(Charsets.UTF_8.decode(bytes).toString())
        start = index
        }
    return list
    }
