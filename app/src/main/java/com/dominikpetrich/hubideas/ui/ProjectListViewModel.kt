package com.dominikpetrich.hubideas.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dominikpetrich.hubideas.data.local.AppDatabase
import com.dominikpetrich.hubideas.data.local.dao.NoteDao
import com.dominikpetrich.hubideas.data.local.entity.NoteEntity
import com.dominikpetrich.hubideas.data.local.entity.ProjectEntity
import com.dominikpetrich.hubideas.data.repo.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProjectListViewModel(
    private val repo: ProjectRepository,
    private val noteDao: NoteDao
) : ViewModel() {

    val projects: StateFlow<List<ProjectEntity>> = repo.projects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addProject(name: String) = viewModelScope.launch {
        val t = name.trim()
        if (t.isEmpty()) return@launch
        val id = repo.add(t)
        // Immer automatisch eine Notiz im Projekt mit gleichem Inhalt erzeugen
        noteDao.insert(NoteEntity(content = t, projectId = id))
    }

    fun rename(id: Long, name: String) = viewModelScope.launch {
        val t = name.trim(); if (t.isNotEmpty()) repo.rename(id, t, null)
    }

    fun delete(id: Long) = viewModelScope.launch { repo.moveToTrash(id) }
}

class ProjectListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getInstance(context)
        val repo = com.dominikpetrich.hubideas.data.repo.ProjectRepository(db.projectDao(), db.noteDao(), db.todoDao())
        @Suppress("UNCHECKED_CAST")
        return ProjectListViewModel(repo, db.noteDao()) as T
    }
}
