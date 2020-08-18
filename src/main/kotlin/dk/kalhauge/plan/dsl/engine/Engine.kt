package dk.kalhauge.plan.dsl.engine

import dk.kalhauge.calendar.dsl.calendar
import dk.kalhauge.calendar.dsl.event
import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.dsl.structure.Block
import dk.kalhauge.document.dsl.structure.Tree
import dk.kalhauge.plan.dsl.*
import dk.kalhauge.plan.dsl.ActivityType.*
import dk.kalhauge.plan.dsl.Material.Category.*
import dk.kalhauge.plan.dsl.Taxonomy.*

fun lectureLink(lecture: Lecture) =
  if (lecture.week.active) text {
    text("${lecture.code} ")
    reference("../week-${lecture.week.code}/${Week.documentName}/L${lecture.code}", title = lecture.title)
    }
  else text("${lecture.code} ${lecture.title}")

fun shortLectureLink(lecture: Lecture) =
  if (lecture.week.active) text {
    reference("../week-${lecture.week.code}/${Week.documentName}/L${lecture.code}", title = lecture.code)
    }
  else text(lecture.code)

fun taxonomiHeader(taxonomy: Taxonomy) = when (taxonomy) {
  KNOWLEDGE -> "/knows/"
  ABILITY -> "is /able/ to"
  SKILL -> "have the /skills/ to"
  // SKILL -> "/masters/"
  }

fun activityHeader(type: ActivityType) = when (type) {
  READ -> "*Read*"
  WRITE -> "*Write*"
  WORK -> "*Do*"
  }

fun Folder.calendarFor(course: Course) {
  val calendar = calendar {
    course.lectures.forEach { lecture ->
      event(
          "${course.label}/W${lecture.week.code}/L${lecture.code}",
          lecture.start,
          lecture.end,
          course.title
          ) {
        description = """
          ${lecture.title}
          """.trimIndent()
        location = lecture.timeSlot.location.name
        // url = "https://datsoftlyngby.github.io/soft2020spring/${course.label}/week-${lecture.week.code}/"
        }
      }
    }
  file("calendar.ical", calendar.toString())
  }


fun Tree.Trunk.add(course: Course) {
  folder(course.label) {
    if (course.calendar != null) calendarFor(course)
    course.weeks.forEach { week ->
      document("week-${week.code}/${Week.documentName}", week.title) {
        paragraph { text {
          week.previous?.let { prev ->
            text(":point__left: ")
            reference("../../week-${prev.code}/${Week.documentName}")
            }
          reference("../../${Course.documentName}", title = " :point__up: ")
          week.next?.let { next ->
            reference("../../week-${next.code}/${Week.documentName}")
            text(" :point__right:")
            }
          } }
        add(week.overview)
        week.lectures.forEach { lecture ->
          section(lecture.title, "L${lecture.code}", number = lecture.number) {
            paragraph("*Time:* ${lecture.timeSlot.dayText} ${lecture.timeSlot.timeText}")
            paragraph("*Location:* ${lecture.timeSlot.location}")
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
                    paragraph {
                      bold("${taxonomiHeader(objective.level)} ").add(objective.title)
                      }
                  else
                    paragraph {
                      text("${taxonomiHeader(objective.level)} ").add(objective.title)
                      }
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
                      val target = activity.target
                      text {
                        if (target == null) add(activity.title)
                        else reference(target, activity.title)
                        text(" - (${activity.load})")
                        }
                      }
                    else -> paragraph {
                      text("${activityHeader(activity.type)} ").apply {
                        add(activity.title)
                        text(" - (${activity.load})")
                        }
                      }
                    }
                  }
                paragraph("In class activities - (${lecture.timeSlot.load})")
                list {
                  lecture.materials.filter { it.category == EXERCISE }.forEach {
                    paragraph { reference(it.target) }
                    }
                  }
                }
              }
            add(lecture.content)
            section("Materials") {
              list {
                lecture.materials.forEach { material ->
                  paragraph {
                    when (material.category) {
                      REPOSITORY -> text(":octocat: ") { reference(material.target) }
                      PRESENTATION -> text(":bar__chart: ") { reference(material.target) }
                      RECORDING -> text(":movie__camera: ") { reference(material.target) }
                      EXERCISE -> text(":pencil: ") { reference(material.target) }
                      LOCAL -> { text(":page__facing__up:") { reference(material.target) } }
                      EXTERNAL -> { text(":globe__with__meridians:") {  reference(material.target) } }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    document(Course.documentName, course.title) {
      toc(2)
      course.curriculum?.let {
        paragraph {
          reference("../curriculum", "Find the curriculum here")
          }
        }
      add(course.overview)
      course.plan {
        course.flows.forEach { flow ->
          section(flow.title) {
            add(flow.overview)
            if (flow.skills != null) paragraph("*Business skills*: ${flow.skills}")
            table {
              center("Week")
              center("Day")
              center("Time")
              left("Subject")
              right("Load")
              left("Notes")
              flow.lectures.forEach { lecture ->
                row {
                  paragraph { reference("../week-${lecture.week.code}/${Week.documentName}", lecture.week.code) }
                  if (lecture.isAsScheduled) {
                    paragraph(lecture.timeSlot.dayText)
                    paragraph(lecture.timeSlot.timeText)
                    }
                  else {
                    paragraph("*${lecture.timeSlot.dayText}*")
                    paragraph("*${lecture.timeSlot.timeText}*")
                    }
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
      add(course.plan)
      section("Resources") {
        var count =
                 courseResourceSection(course.materials, "Presentations", PRESENTATION)
        count += courseResourceSection(course.materials, "Exercises", EXERCISE)
        count += courseResourceSection(course.materials, "Repositories", REPOSITORY)
        count += courseResourceSection(course.materials, "External Links", EXTERNAL)
        if (count == 0) paragraph("Selected resources from lectures will show here.")
        section("Literature") {
          list {
            course.books.forEach { book ->
              list {
                capture(book.title, label = book.label) {
                  if (book.subtitle != null) text("/${book.subtitle}/")
                  if (book.edition != null) text("${book.edition} edition")
                  text("*${book.authors}* - /${book.editor}/")
                  if (book.isbn != null) text("`${book.isbn}`")
                  if (book.url != null) { website(book.url) }
                  }
                }
              }
            }
          }
        }
      section("Assignments and Credits") {
        add(course.creditable)
        table {
          left("Title")
          right("Credits")
          course.creditables.forEach { creditable ->
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
      if (course.objectives.isNotEmpty()) {
        section("Curriculum") {
          add(course.objective)
          lectureObjectiveSection(course.lectures, "Knowledge (/Viden/)", KNOWLEDGE)
          lectureObjectiveSection(course.lectures, "Abilities (/Færdigheder/)", ABILITY)
          lectureObjectiveSection(course.lectures, "Skills (/Kompetencer/)", SKILL)
          }
        }
      add(course.exam)
      if (course.calendar != null)
          paragraph("Calendar subscription link: `https://datsoftlyngby.github.io/${course.calendar}/${course.label}/calendar.ical`")
      }
    course.curriculum?.let { curriculum ->
      document("curriculum", course.title) {
/*
        section("Indhold") { add(curriculum.content) }
        curriculumObjectiveSection(curriculum, KNOWLEDGE, "Viden /knowledge/", "Den studerende har viden om:")
        curriculumObjectiveSection(curriculum, ABILITY, "Færdigheder /abilities/", "Den studerende kan:")
        curriculumObjectiveSection(curriculum, SKILL, "Kompetencer /skills/", "Den studerende kan:")
*/
        section("Content") { add(curriculum.content) }
        curriculumObjectiveSection(curriculum, KNOWLEDGE, "Knowledge /viden/", "The student has knowledge about:")
        curriculumObjectiveSection(curriculum, ABILITY, "Abilities /færdigheder/", "The student can:")
        curriculumObjectiveSection(curriculum, SKILL, "Skills /kompetencer/", "The student can:")
        }
      }
    document("summary", "${course.title} - Summary") {
      section("Credits") {
        table {
          left("Title")
          right("Credits")
          course.creditables.forEach { creditable ->
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
            paragraph("*${course.creditables.map {it.credits }.sum()}*")
            }
          }
        }
      course.curriculum?.let { curriculum ->
        section("Course Objectives /the Matrix/") {
          table {
            left("Code")
            left("Objective")
            right("Lecture")
            curriculum.objectives.values.flatten().forEach { objective ->
              row {
                paragraph(objective.key)
                paragraph(objective.title)
                paragraph {
                  course.lectures
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
        lectureObjectiveSection(course.lectures, "Knowledge (/Viden/)", KNOWLEDGE)
        lectureObjectiveSection(course.lectures, "Abilities (/Færdigheder/)", ABILITY)
        lectureObjectiveSection(course.lectures, "Skills (/Kompetencer/)", SKILL)
        }
      section("Work Load") {
        table {
          left("Lecture")
          right("/All/")
          right("Class")
          right("Read")
          right("Write")
          right("Do")
          course.lectures.forEach {
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
              course.lectures.map { it.loads() }.fold(loadSum) { sum, load -> sum.added(load) }
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
        course.flows.filter { it.skills != null }.forEach {
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
    }
  }

fun Block.BaseParent.courseResourceSection(
    materials: List<Material>,
    title: String,
    category: Material.Category
    ): Int {
  val specifics = materials.filter { it.category == category }
  if (specifics.isEmpty()) return 0
  section(title) {
    list {
      specifics.forEach {
        paragraph { reference(it.target) }
        }
      }
    }
  return specifics.size
  }

fun Block.BaseParent.curriculumObjectiveSection(
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

fun Block.BaseParent.lectureObjectiveSection(lectures: List<Lecture>, title: String, taxonomy: Taxonomy) {
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
  when (this.size) {
    1 -> this[0]
    2 -> "${this[0]} and ${this[1]}"
    else -> "${this.take(this.size - 1).joinToString(", ")}, and ${this.last()}"
    }

fun Document.courseList(trunk: Tree.Trunk? = null, documentName: String = "README") {
  val folder = trunk ?: this.trunk
  list {
    folder.branches.filterIsInstance<Folder>().forEach { folder ->
      val courseDocument =
        folder.branches
          .filterIsInstance<Document>()
          .firstOrNull { it.name == documentName }
      if (courseDocument != null)
        paragraph { reference(courseDocument) }
      }
    }
  }

fun courseLectureLink(lecture: Lecture) =
  if (lecture.week.active) text {
    text("`${lecture.timeSlot.startText}` ")
    reference("../${lecture.course.label}/week-${lecture.week.code}/${Week.documentName}/L${lecture.code}", title = lecture.course.label)
    }
  else text("`${lecture.timeSlot.startText}` ${lecture.course.label}")

fun Document.schedule(semester: Semester, weekNumbers: IntRange? = null) {
  val grid = Grid<Int, WeekDay, Lecture>(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY)
  semester.courses.flatMap { it.lectures }.forEach {
    grid[it.week.number, it.timeSlot.weekDay] = it
    }
  val numbers = weekNumbers?.toList() ?: grid.rows.keys.toList()
  this.table {
    left("Week")
    left(WeekDay.MONDAY.name)
    left(WeekDay.TUESDAY.name)
    left(WeekDay.WEDNESDAY.name)
    left(WeekDay.THURSDAY.name)
    left(WeekDay.FRIDAY.name)
    //grid.rows.forEach { weekNumber, _ ->
    numbers.forEach { weekNumber ->
      row {
        paragraph("$weekNumber")
        grid.columnKeys.forEach { weekDay ->
          paragraph {
            text(" ")
            grid[weekNumber, weekDay].forEachIndexed { index, lecture ->
              if (index > 0) text(" <br> ")
              add(courseLectureLink(lecture)) }
            }
          }
        }
      }
    }
  }