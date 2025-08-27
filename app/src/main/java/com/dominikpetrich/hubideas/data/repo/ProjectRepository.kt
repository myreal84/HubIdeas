package com.dominikpetrich.hubideas.data.repo

import com.dominikpetrich.hubideas.data.local.dao.ProjectDao
import com.dominikpetrich.hubideas.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val dao: ProjectDao) {
    companion object { const val INBOX_ID = 1L }

    val projects: Flow<List<ProjectEntity>> = dao.getAll()

    suspend fun add(name: String, description: String? = null): Long =
        dao.insert(ProjectEntity(name = name, description = description))

    suspend fun rename(id: Long, name: String, description: String?) =
        dao.update(id = id, name = name, description = description, updatedAt = System.currentTimeMillis())

    suspend fun delete(id: Long) {
        require(id != INBOX_ID) { "Inbox kann nicht gelöscht werden" }
        dao.deleteById(id)
    }
}
