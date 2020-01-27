package testarea.experiments

import java.io.File
import kotlin.reflect.KClass

fun MutableMap<String, String>.loadProperties(klass: KClass<*>, name: String) {
  klass::class.java.classLoader.getResourceAsStream(name).bufferedReader().use { reader ->
    reader.lines().forEach { line ->
      val parts = line.split('=', ':').map { it.trim().substringBefore("#") }
      when (parts.size) {
        0 -> { }
        1 -> this[parts[0]] = ""
        else -> this[parts[0]] = parts[1]
        }
      }
    }
  }

fun MutableMap<String, String>.saveProperties(klass: KClass<*>, name: String) {
  val path = klass::class.java.classLoader.getResource(".").path
  println(path)
  val file = File(path, "../../../../src/main/resources/$name")

  file.outputStream().bufferedWriter().use { writer ->
    this.forEach { (key, value) ->
      writer.write("$key=$value")
      writer.newLine()
      }
    writer.flush()
    }
  }

class Klass

fun main() {
  println(Runtime.getRuntime())
  return
  val properties = mutableMapOf<String, String>()
  properties.loadProperties(Klass::class, "course.properties")
  println(properties["course.root"])
  println("---")
  println(properties)
  properties["course-year"] = "2020"
  println("---")
  println(properties)
  properties.saveProperties(Klass::class, "course.properties")
  }