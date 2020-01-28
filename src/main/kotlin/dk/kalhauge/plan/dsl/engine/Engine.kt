package dk.kalhauge.plan.dsl.engine

import dk.kalhauge.document.dsl.*
import dk.kalhauge.plan.dsl.*
import dk.kalhauge.plan.dsl.Material.Category.*
import dk.kalhauge.plan.dsl.Taxonomy.*
import dk.kalhauge.plan.dsl.ActivityType.*

fun lectureLink(lecture: Lecture) =
  if (lecture.week.active) text {
    text("${lecture.code} ")
    reference("../week-${lecture.week.code}/info/sec:L${lecture.code}", title = lecture.title)
    }
  else text("${lecture.code} ${lecture.title}")

fun shortLectureLink(lecture: Lecture) =
  if (lecture.week.active) text {
    reference("../week-${lecture.week.code}/info/sec:L${lecture.code}", title = lecture.code)
    }
  else text("${lecture.code}")

fun taxonomiHeader(taxonomy: Taxonomy) = when (taxonomy) {
  KNOWLEDGE -> "/knows/"
  ABILITY -> "is /able/ to"
  // SKILL -> "have the /skills/ to"
  SKILL -> "/masters/"
  }

fun activityHeader(type: ActivityType) = when (type) {
  READ -> "*Read*"
  WRITE -> "*Write*"
  WORK -> "*Do*"
  }

val Course.context: Context get() =
  folder(label, folder) {
    weeks.forEach { week ->
      document("week-${week.code}/info", week.title) {
        paragraph { text {
          week.previous?.let { prev ->
            text(":point__left: ")
            reference("../../week-${prev.code}/info/top")
            }
          reference("../../course-info/top", title = " :point__up: ")
          week.next?.let { next ->
            reference("../../week-${next.code}/info/top")
            text(" :point__right:")
            }
          } }
        add(week.overview)
        week.lectures.forEach { lecture ->
          section(lecture.title, "L${lecture.code}") {
            when (lecture.teachers.size) {
              0 -> {}
              1 -> paragraph("*Teacher:* ${lecture.teachers[0].name}")
              else -> paragraph("*Teachers:* ${lecture.teachers.joinToString(", ") { it.name }}")
              }
            add(lecture.overview)
            section("Objectives") {
              add(lecture.objective)
              list {
                lecture.objectives.sortedBy { it.level }.forEach { objective ->
                  if (objective.fromCurriculum)
                    paragraph("*${taxonomiHeader(objective.level)} ${objective.title}*")
                  else
                    paragraph("${taxonomiHeader(objective.level)} ${objective.title}")
                  }
                }
              }
            val load = lecture.activities.map { it.load }.sum() + lecture.timeSlot.load
            section("Teaching and Learning Activities ($load)") {
              add(lecture.activity)
              list {
                lecture.activities.forEach { activity ->
                  when (activity) {
                    is Assignment -> paragraph {
                      text {
                        reference(activity.target!!, activity.title)
                        text(" - (${activity.load})")
                        }
                      }
                    else -> paragraph("${activityHeader(activity.type)} ${activity.title} - (${activity.load})")
                    }
                  }
                paragraph("In class activities - (${lecture.timeSlot.load})")
                list {
                  lecture.materials.filter { it.category == Material.Category.EXERCISE }.forEach {
                    paragraph { reference(it.resource) }
                    }
                  }
                }
              }
            section("Materials") {
              list {
                lecture.materials.forEach { material ->
                  paragraph {
                    when (material.category) {
                      REPOSITORY -> text(":octocat: ") { reference(material.resource) }
                      PRESENTATION -> text(":bar__chart: ") { reference(material.resource) }
                      EXERCISE -> text(":pencil: ") { reference(material.resource) }
                      LOCAL -> { }
                      EXTERNAL -> { }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    document("course-info", "$title - $semester") {
      toc(1)
      curriculum?.let {
        paragraph {
          reference("../curriculum/top", "Find the curriculum here (Danish)")
          }
        }
      add(overview)
      /*
      section("Objectives") {
        add(objective)
        list {
          objectives.values.sortedBy { it.level }.forEach { objective ->
            if (objective.fromCurriculum)
                paragraph("*${taxonomiHeader(objective.level)} ${objective.title}*")
            else
                paragraph("${taxonomiHeader(objective.level)} ${objective.title}")
            }
          }
        }
      */
      section("Plan") {
        add(plan)
        flows.forEach { flow ->
          section(flow.title) {
            add(flow.overview)
            if (flow.skills != null) paragraph { +"*Business skills*: ${flow.skills}" }
            table {
              center("Week")
              center("Day")
              center("Time")
              left("Subject")
              right("Load")
              left("Notes")
              flow.lectures.forEach { lecture ->
                row {
                  paragraph { reference("../week-${lecture.week.code}/info/top", lecture.week.code) }
                  paragraph(lecture.timeSlot.dayText)
                  paragraph(lecture.timeSlot.timeText)
                  paragraph { add(lectureLink(lecture)) }
                  paragraph("${lecture.workLoad}")
                  paragraph{
                    lecture.teachers.forEach { text(it.initials) }
                    text(lecture.note)
                    }
                  }
                }
              }
            }
          }
        }
      section("Resources") {
        courseResourceSection(materials, "Presentations", PRESENTATION)
        courseResourceSection(materials, "Exercises", EXERCISE)
        courseResourceSection(materials, "Repositories", REPOSITORY)
        courseResourceSection(materials, "External Links", EXTERNAL)
        }
      section("Assignments and Credits") {
        add(creditable)
        table {
          left("Title")
          right("Credits")
          creditables.forEach { creditable ->
            row {
              when (creditable) {
                is Assignment -> {
                  paragraph {
                    creditable.target?.also {
                      reference(it, creditable.title)
                      } ?: add(creditable.title)
                    }
                  paragraph("${creditable.credits}")
                  }
                else -> {
                  paragraph(creditable.title)
                  paragraph("${creditable.credits}")
                  }
                }
              }
            }
          }
        }
      add(exam)
      }
    curriculum?.let { curriculum ->
      document("curriculum", "$title") {
        section("Indhold") { add(curriculum.content) }
        curriculumObjectiveSection(curriculum, KNOWLEDGE, "Viden /knowledge/", "Den studerende har viden om:")
        curriculumObjectiveSection(curriculum, ABILITY, "Færdigheder /abilities/", "Den studerende kan:")
        curriculumObjectiveSection(curriculum, SKILL, "Kompetencer /skills/", "Den studerende kan:")
        }
      }
    document("summary", "$title - Summary") {
      section("Credits") {
        table {
          left("Title")
          right("Credits")
          creditables.forEach { creditable ->
            row {
              when (creditable) {
                is Assignment -> {
                  paragraph {
                    creditable.target?.also {
                      reference(it, creditable.title)
                      } ?: add(creditable.title)
                    }
                  paragraph("${creditable.credits}")
                  }
                else -> {
                  paragraph(creditable.title)
                  paragraph("${creditable.credits}")
                  }
                }
              }
            }
          row {
            paragraph("*Total*")
            paragraph("*${creditables.map {it.credits }.sum()}*")
            }
          }
        }
      curriculum?.let { curriculum ->
        section("Course Objectives /the Matrix/") {
          table {
            left("Code")
            left("Objective")
            right("Lecture")
            curriculum.objectives.values.flatMap { it }.forEach { objective ->
              row {
                paragraph(objective.key)
                paragraph(objective.title)
                paragraph {
                  lectures
                    .filter { lecture ->
                      lecture.objectives.any { it.fulfillments.contains(objective.key) }
                      }
                    .forEach {
                      add(shortLectureLink(it))
                      }
                  }
                }
              }
            }
          }
        }
      section("Lecture Objectives") {
        lectureObjectiveSection(lectures, "Knowledge (/Viden/)", KNOWLEDGE)
        lectureObjectiveSection(lectures, "Abilities (/Færdigheder/)", ABILITY)
        lectureObjectiveSection(lectures, "Skills (/Kompetencer/)", SKILL)
        }
      section("Work Load") {
        table {
          left("Lecture")
          right("/All/")
          right("Class")
          right("Read")
          right("Write")
          right("Do")
          lectures.forEach {
            val (presence, read, write, work) = it.loads()
            row {
              paragraph { add(lectureLink(it)) }
              paragraph("/${(presence + read + write + work)}/")
              paragraph(presence.toString())
              paragraph(read.toString())
              paragraph(write.toString())
              paragraph(work.toString())
              }
            }
          val loadSum = Lecture.Load()
          val (presence, read, write, work) =
              lectures.map { it.loads() }.fold(loadSum) { sum, load -> sum.added(load) }
          row {
            paragraph("*Total*")
            paragraph("*/${(presence + read + write + work)}/*")
            paragraph("*$presence*")
            paragraph("*$read*")
            paragraph("*$write*")
            paragraph("*$work*")
            }
          }
        }
      section("Business Skills") {
        flows.filter { it.skills != null }.forEach {
          // TODO add Text as type to bold(...)
          paragraph {
            text {
              bold(it.title)
              text(": ")
              add(it.skills!!)
              }
            }
          }
        }
      }
    }.run { Context.root }

fun Block.Parent.courseResourceSection(materials: List<Material>, title: String, category: Material.Category) {
  section(title) {
    list {
      materials.filter { it.category == category }.forEach {
        paragraph { reference(it.resource) }
        }
      }
    }
  }

fun Block.Parent.curriculumObjectiveSection(
  curriculum: Curriculum,
  taxonomy: Taxonomy,
  title: String,
  subtitle: String
    ) {
  curriculum.objectives[taxonomy]?.let { items ->
    section(title) {
      paragraph(subtitle)
      list {
        items.forEach { paragraph("${it.key}: ${it.title}") }
        }
      }
    }
  }

fun Block.Parent.lectureObjectiveSection(lectures: List<Lecture>, title: String, taxonomy: Taxonomy) {
  section(title) {
    table {
      left("Objective")
      right("Lecture")
      lectures.forEach { lecture ->
        lecture.objectives
            .filter { it.level == taxonomy }
            .forEach { objective ->
          row {
            if (objective.fromCurriculum)
              paragraph("*${taxonomiHeader(objective.level)} ${objective.title}*")
            else
              paragraph("${taxonomiHeader(objective.level)} ${objective.title}")
            paragraph { add(shortLectureLink(lecture)) }
            }
          }
        }
      }
    }
  }

fun List<String>.joinEnglish() =
  if (this.size == 1) this[0]
  else if (this.size == 2) "${this[0]} and ${this[1]}"
  else "${this.take(this.size - 1).joinToString(", ")}, and ${this.last()}"