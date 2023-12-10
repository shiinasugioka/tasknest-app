package edu.uw.ischool.shiina12.tasknest

import android.app.Application
import android.util.Log

class TaskApp : Application() {
    private val TAG : String = "TaskApp"
    /*val todoRepository: TodoRepository = InMemoryTodoRepository()
    var todoNests: MutableList<TodoNest> = mutableListOf()*/

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "TaskApp is being loaded and run.")
        // todoNests = todoRepository.getNest()
    }

    companion object {
        lateinit var instance: TaskApp
            private set
    }
}