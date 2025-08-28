package com.dominikpetrich.hubideas.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dominikpetrich.hubideas.data.local.AppDatabase
import com.dominikpetrich.hubideas.data.local.entity.ProjectEntity
import com.dominikpetrich.hubideas.data.repo.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TrashViewModel(private val repo: ProjectRepository) : ViewModel() {
    val trashed: StateFlow<List<ProjectEntity>> = repo.trashed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun restore(id: Long) = viewModelScope.launch { repo.restoreFromTrash(id) }
    fun hardDelete(id: Long) = viewModelScope.launch { repo.hardDelete(id) }
}

class TrashViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getInstance(context)
        val repo = com.dominikpetrich.hubideas.data.repo.ProjectRepository(db.projectDao(), db.noteDao(), db.todoDao())
        @Suppress("UNCHECKED_CAST")
        return TrashViewModel(repo) as T
    }
}
