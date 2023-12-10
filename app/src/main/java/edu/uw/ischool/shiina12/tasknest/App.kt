package edu.uw.ischool.shiina12.tasknest

import android.app.Application

class App : Application() {
    val todoRepository: InMemoryTodoRepository by lazy {
        InMemoryTodoRepository()
    }
}
