package com.dominikpetrich.hubideas.data.repo

import com.dominikpetrich.hubideas.data.local.dao.NoteDao
import com.dominikpetrich.hubideas.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    private companion object { const val INBOX_ID = 1L }

    val notes: Flow<List<NoteEntity>> = dao.getAll()

    suspend fun add(content: String) = dao.insert(NoteEntity(content = content, projectId = INBOX_ID))
    suspend fun rename(id: Long, content: String) = dao.rename(id, content)
    suspend fun delete(id: Long) = dao.deleteById(id)
}
