package com.dominikpetrich.hubideas.data.local.dao

import androidx.room.*
import com.dominikpetrich.hubideas.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects WHERE trashedAt IS NULL ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE trashedAt IS NOT NULL ORDER BY trashedAt DESC")
    fun getTrashed(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<ProjectEntity>

    @Query("SELECT * FROM projects WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): ProjectEntity?

    @Insert
    suspend fun insert(project: ProjectEntity): Long

    @Query("UPDATE projects SET name = :name, description = :description, updatedAt = :updatedAt WHERE id = :id")
    suspend fun update(id: Long, name: String, description: String?, updatedAt: Long)

    @Query("UPDATE projects SET isDone = :done, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean, updatedAt: Long)

    @Query("UPDATE projects SET trashedAt = :ts, updatedAt = :ts WHERE id = :id")
    suspend fun markTrashed(id: Long, ts: Long)

    @Query("UPDATE projects SET trashedAt = NULL, updatedAt = :ts WHERE id = :id")
    suspend fun restore(id: Long, ts: Long)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteById(id: Long)
}
