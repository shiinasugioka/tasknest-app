package edu.uw.ischool.shiina12.tasknest

interface TodoRepository {
    fun createTodoList(nestName: String): TodoNest
    fun addTaskToList(nest: TodoNest, task: Task)
    fun modifyTask(nest: TodoNest, taskTitle: String, updatedTask: Task)
    fun deleteTask(nest: TodoNest, taskTitle: String)
    fun markTaskAsFinished(nest: TodoNest, taskTitle: String)
    fun createMultipleTodoLists(): List<TodoNest>
    fun viewTodoList(nest: TodoNest): List<Task>
    fun changeTodoListSorting(nest: TodoNest, sortingMethod: SortingMethod)
    fun setTaskDeadline(task: Task, deadline: Long)
    fun getNotificationsForTask(task: Task, notificationType: NotificationType)
    fun exportTasksToJson(nest: TodoNest): String
}

data class TodoNest(
    val title: String,
    val tasks: MutableList<Task> = mutableListOf()
)


data class Task(
    var title: String,
    var description: String,
    var deadline: Long? = null,
    var isFinished: Boolean = false
)

enum class SortingMethod {
    BY_DEADLINE,
    BY_CREATION_TIME
}

enum class NotificationType {
    APP,
    SMS,
    EMAIL
}
