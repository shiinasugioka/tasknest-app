// InMemoryTodoRepository.kt
package edu.uw.ischool.shiina12.tasknest

class InMemoryTodoRepository : TodoRepository {
    private val todoNests: MutableList<TodoNest> = mutableListOf(
        TodoNest(
            title = "Personal",
            tasks = mutableListOf(
                Task(title = "Buy groceries", description = "Milk, eggs, bread"),
                Task(title = "Exercise", description = "Go for a run")
            )
        ),
        TodoNest(
            title = "Work",
            tasks = mutableListOf(
                Task(title = "Finish report", description = "Due by end of the day"),
                Task(title = "Meeting with team", description = "Discuss project updates")
            )
        )
    )

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
