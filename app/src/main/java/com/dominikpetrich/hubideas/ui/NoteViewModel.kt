package com.dominikpetrich.hubideas.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dominikpetrich.hubideas.data.local.AppDatabase
import com.dominikpetrich.hubideas.data.repo.NoteRepository
import com.dominikpetrich.hubideas.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repo: NoteRepository) : ViewModel() {
    val notes: StateFlow<List<NoteEntity>> = repo.notes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(content: String) = viewModelScope.launch { repo.add(content) }
    fun rename(id: Long, content: String) = viewModelScope.launch { repo.rename(id, content) }
    fun delete(id: Long) = viewModelScope.launch { repo.delete(id) }
}

class NoteViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getInstance(context)
        val repo = NoteRepository(db.noteDao())
        @Suppress("UNCHECKED_CAST")
        return NoteViewModel(repo) as T
    }
}
