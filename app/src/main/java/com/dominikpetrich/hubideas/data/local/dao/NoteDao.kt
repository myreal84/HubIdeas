package com.dominikpetrich.hubideas.data.local.dao

import androidx.room.*
import com.dominikpetrich.hubideas.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAll(): Flow<List<NoteEntity>>

    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Query("UPDATE notes SET content = :content WHERE id = :id")
    suspend fun rename(id: Long, content: String)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
