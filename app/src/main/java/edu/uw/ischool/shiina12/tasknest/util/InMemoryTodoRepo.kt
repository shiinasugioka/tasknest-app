// InMemoryTodoRepository.kt
package edu.uw.ischool.shiina12.tasknest.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

import java.util.Calendar

// Obtain today's date at midnight as a Long
val today = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis // This is a Long value

object InMemoryTodoRepository : TodoRepository {
    val todoNests: MutableList<TodoNest> = mutableListOf()
    var currNest = ""

    override fun getNests(): MutableList<TodoNest> {
        return todoNests
    }

    override fun getTodoNestByTitle(nestTitle: String): TodoNest? {
        return todoNests.find { it.title == nestTitle }
    }

    override fun getAllNestTitles(): Array<String> {
        return todoNests.map { it.title }.toTypedArray()
    }

    fun getTasksForToday(): List<Task> {
        val today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return todoNests.flatMap { nest ->
            nest.tasks.filter { it.deadline != null && it.deadline!! >= today && it.deadline!! < today + 86400000 }
        }
    }

    fun getTasksForThisWeek(): List<Task> {
        val (startOfWeek, endOfWeek) = getWeekRange()
        return todoNests.flatMap { nest ->
            nest.tasks.filter { it.deadline != null && it.deadline!! >= startOfWeek && it.deadline!! <= endOfWeek }
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
            it.description = updatedTask.description
            it.deadline = updatedTask.deadline
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

    override fun setTaskDeadline(task: Task, deadline: Long) {
        task.deadline = deadline
    }

    override fun getNotificationsForTask(task: Task, notificationType: NotificationType) {
        // TODO Implementation for notifications
    }

    override fun exportTasksToJson(nest: TodoNest): String {
        // TODO Implementation for exporting tasks to JSON
        return ""
    }
}