// InMemoryTodoRepository.kt
package edu.uw.ischool.shiina12.tasknest.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.Instant
import java.time.ZonedDateTime

import java.util.Calendar
import java.util.Date
import java.util.Locale

// Obtain today's date at midnight as a Long
//val today = Calendar.getInstance().apply {
//    set(Calendar.HOUR_OF_DAY, 0)
//    set(Calendar.MINUTE, 0)
//    set(Calendar.SECOND, 0)
//    set(Calendar.MILLISECOND, 0)
//}.timeInMillis // This is a Long value

object InMemoryTodoRepository : TodoRepository {

    val todoNests: MutableList<TodoNest> = mutableListOf()
    private var currNest = ""

    private var currentDate: String

    init {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        currentDate = dateFormat.format(Date()).toString()

        val defaultTask = Task(
            title = "Sample Task",
            apiDateTime = currentDate, // or any other default deadline
            isFinished = false,
            displayableStartTime = "",
            displayableStartDate = ""
        )

        val defaultTodoNest = TodoNest(
            title = "Default Nest",
            tasks = mutableListOf(defaultTask) // Add the default task to the default nest
        )

        todoNests.add(defaultTodoNest)
    }

    override fun getNests(): MutableList<TodoNest> {
        return todoNests
    }

    override fun getTodoNestByTitle(nestTitle: String): TodoNest? {
        return todoNests.find { it.title == nestTitle }
    }

    override fun getAllNestTitles(): Array<String> {
        return todoNests.map { it.title }.toTypedArray()
    }

    fun getTasksFromNest(todoNest: TodoNest): List<Task> {
        return todoNest.tasks
    }

    fun getTasksForToday(): List<Task> {
//        val today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
//        return todoNests.flatMap { nest ->
//            nest.tasks.filter { it.apiDateTime >= currentDate && it.apiDateTime!! < (currentDate as Long) + 86400000 }
//        }

        val todayInstant = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(currentDate))
        val todayDate = ZonedDateTime.ofInstant(todayInstant, ZoneId.systemDefault()).toLocalDate()

        return todoNests.flatMap { nest ->
            nest.tasks.filter { task ->
                val taskInstant = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(task.apiDateTime))
                val taskDate = ZonedDateTime.ofInstant(taskInstant, ZoneId.systemDefault()).toLocalDate()
                taskDate.isEqual(todayDate)
            }
        }

    }

    fun setCurrNestName(nestName: String) {
        this.currNest = nestName
    }

    fun getCurrNestName(): String {
        return currNest
    }

    fun removeNest(nestName: String) {
        todoNests.remove(getTodoNestByTitle(nestName))
    }

    fun renameNest(oldName: String, newName: String) {
        getTodoNestByTitle(oldName)?.title = newName
    }

    private fun getWeekRange(): Pair<Long, Long> {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            .atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli() + 86400000 - 1 // End of Sunday
        // Log.d("WeekRange", "Start: $startOfWeek, End: $endOfWeek") // Uncomment if logging is needed
        return Pair(startOfWeek, endOfWeek)
    }

    override fun createTodoList(nestName: String): TodoNest {
        val newTodoNest = TodoNest(title = nestName)
        todoNests.add(newTodoNest)
        return newTodoNest
    }

    override fun addTaskToList(nest: TodoNest, task: Task) {
        nest.tasks.add(task)
    }

    override fun modifyTask(nest: TodoNest, taskTitle: String, updatedTask: Task) {
        val existingTask = nest.tasks.find { it.title == taskTitle }
        existingTask?.let {
            it.title = updatedTask.title
            it.apiDateTime = updatedTask.apiDateTime
            it.isFinished = updatedTask.isFinished
        }
    }

    override fun deleteTask(nest: TodoNest, taskTitle: String) {
        nest.tasks.removeAll { it.title == taskTitle }
    }

    override fun markTaskAsFinished(nest: TodoNest, taskTitle: String) {
        val task = nest.tasks.find { it.title == taskTitle }
        task?.isFinished = true
    }

    override fun createMultipleTodoLists(): List<TodoNest> {
        return todoNests.toList()
    }

    override fun viewTodoList(nest: TodoNest): List<Task> {
        return nest.tasks
    }

    override fun changeTodoListSorting(nest: TodoNest, sortingMethod: SortingMethod) {
        // TODO Implementation for changing sorting method
    }

    override fun setTaskDeadline(task: Task, deadline: String) {
        task.apiDateTime = deadline
    }

    override fun getNotificationsForTask(task: Task, notificationType: NotificationType) {
        // TODO Implementation for notifications
    }

    override fun exportTasksToJson(nest: TodoNest): String {
        // TODO Implementation for exporting tasks to JSON
        return ""
    }
}