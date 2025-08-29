package com.dominikpetrich.hubideas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dominikpetrich.hubideas.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE trashedAt IS NULL ORDER BY createdAt DESC")
    fun getAll(): Flow<List<NoteEntity>>

    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Query("UPDATE notes SET content = :content WHERE id = :id")
    suspend fun rename(id: Long, content: String)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE notes SET projectId = :newProjectId WHERE projectId = :oldProjectId")
    suspend fun reassignProject(oldProjectId: Long, newProjectId: Long = 1L)

    @Query("UPDATE notes SET trashedAt = :ts WHERE projectId = :projectId AND trashedAt IS NULL")
    suspend fun markTrashedByProject(projectId: Long, ts: Long)

    @Query("UPDATE notes SET trashedAt = NULL WHERE projectId = :projectId")
    suspend fun restoreByProject(projectId: Long)

    @Query("DELETE FROM notes WHERE projectId = :projectId")
    suspend fun hardDeleteByProject(projectId: Long)
}
