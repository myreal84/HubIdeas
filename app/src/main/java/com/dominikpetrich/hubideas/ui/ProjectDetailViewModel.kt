package com.dominikpetrich.hubideas.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dominikpetrich.hubideas.data.local.AppDatabase
import com.dominikpetrich.hubideas.data.local.entity.ProjectEntity
import com.dominikpetrich.hubideas.data.local.entity.TodoEntity
import com.dominikpetrich.hubideas.data.repo.ProjectRepository
import com.dominikpetrich.hubideas.data.repo.TodoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProjectDetailViewModel(
    private val projectId: Long,
    private val projectRepo: ProjectRepository,
    private val todoRepo: TodoRepository
) : ViewModel() {
    val project: StateFlow<ProjectEntity?> = projectRepo.project(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todos: StateFlow<List<TodoEntity>> = todoRepo.todosOf(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleProject(done: Boolean) = viewModelScope.launch { projectRepo.setDone(projectId, done) }
    fun addTodo(title: String) = viewModelScope.launch { val t = title.trim(); if (t.isNotEmpty()) todoRepo.add(projectId, t) }
    fun toggleTodo(id: Long, done: Boolean) = viewModelScope.launch { todoRepo.setDone(id, done) }
    fun deleteTodo(id: Long) = viewModelScope.launch { todoRepo.delete(id) }
}

class ProjectDetailViewModelFactory(
    private val context: Context,
    private val projectId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getInstance(context)
        val p = ProjectRepository(db.projectDao(), db.noteDao(), db.todoDao())
        val t = TodoRepository(db.todoDao())
        @Suppress("UNCHECKED_CAST")
        return ProjectDetailViewModel(projectId, p, t) as T
    }
}
