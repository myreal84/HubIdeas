package com.dominikpetrich.hubideas.data.local.dao

import androidx.room.*
import com.dominikpetrich.hubideas.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ProjectEntity>>

    @Insert
    suspend fun insert(project: ProjectEntity): Long

    @Query("UPDATE projects SET name = :name, description = :description, updatedAt = :updatedAt WHERE id = :id")
    suspend fun update(id: Long, name: String, description: String?, updatedAt: Long)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteById(id: Long)
}
