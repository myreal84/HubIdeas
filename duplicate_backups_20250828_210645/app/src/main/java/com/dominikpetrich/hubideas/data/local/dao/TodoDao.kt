package com.dominikpetrich.hubideas.data.local.dao

import androidx.room.*
import com.dominikpetrich.hubideas.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE projectId = :projectId AND trashedAt IS NULL ORDER BY createdAt DESC")
    fun getByProject(projectId: Long): Flow<List<TodoEntity>>

    @Insert
    suspend fun insert(todo: TodoEntity): Long

    @Query("UPDATE todos SET isDone = :done WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE todos SET trashedAt = :ts WHERE projectId = :projectId AND trashedAt IS NULL")
    suspend fun markTrashedByProject(projectId: Long, ts: Long)

    @Query("UPDATE todos SET trashedAt = NULL WHERE projectId = :projectId")
    suspend fun restoreByProject(projectId: Long)

    @Query("DELETE FROM todos WHERE projectId = :projectId")
    suspend fun hardDeleteByProject(projectId: Long)
}
