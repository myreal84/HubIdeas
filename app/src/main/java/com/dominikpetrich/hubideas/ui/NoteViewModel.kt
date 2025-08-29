package com.dominikpetrich.hubideas.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.dominikpetrich.hubideas.data.local.entity.NoteEntity

/** Minimaler Stub, damit der Build nicht blockiert. */
class NoteViewModel : ViewModel() {
    fun notes(projectId: Long): Flow<List<NoteEntity>> = emptyFlow()
    suspend fun addNote(projectId: Long, content: String) { /* no-op */ }
}
