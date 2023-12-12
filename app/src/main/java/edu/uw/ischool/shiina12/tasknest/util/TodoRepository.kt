package edu.uw.ischool.shiina12.tasknest.util

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface TodoRepository {
    fun createTodoList(nestName: String): TodoNest
    fun addTaskToList(nest: TodoNest, task: Task)
    fun modifyTask(nest: TodoNest, taskTitle: String, updatedTask: Task)
    fun deleteTask(nest: TodoNest, taskTitle: String)
    fun markTaskAsFinished(nest: TodoNest, taskTitle: String)
    fun createMultipleTodoLists(): List<TodoNest>
    fun viewTodoList(nest: TodoNest): List<Task>
    fun changeTodoListSorting(nest: TodoNest, sortingMethod: SortingMethod)
    fun setTaskDeadline(task: Task, deadline: String)
    fun getNotificationsForTask(task: Task, notificationType: NotificationType)
    fun exportTasksToJson(nest: TodoNest): String
    fun getTodoNestByTitle(nestTitle: String): TodoNest?
    fun getAllNestTitles(): Array<String>
    fun getNests(): MutableList<TodoNest>
}

data class TodoNest(
    var title: String, val tasks: MutableList<Task> = mutableListOf()
)

data class Task(
    var title: String,
    var apiDateTime: String,
    var displayableStartTime: String,
    var displayableStartDate: String,
    var isFinished: Boolean = false,
    val dateCreated: String = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()
    ).format(
        Date()
    ).toString()
) : Serializable

enum class SortingMethod {
    BY_DEADLINE, BY_CREATION_TIME
}

enum class NotificationType {
    APP, SMS, EMAIL
}