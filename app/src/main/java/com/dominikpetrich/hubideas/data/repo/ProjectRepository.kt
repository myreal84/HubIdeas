package com.dominikpetrich.hubideas.data.repo

import com.dominikpetrich.hubideas.data.local.dao.ProjectDao
import com.dominikpetrich.hubideas.data.local.dao.NoteDao
import com.dominikpetrich.hubideas.data.local.dao.TodoDao
import com.dominikpetrich.hubideas.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(
    private val dao: ProjectDao,
    private val noteDao: NoteDao,
    private val todoDao: TodoDao
) {

    fun projects(): Flow<List<ProjectEntity>> = dao.getAll()
    fun trashed(): Flow<List<ProjectEntity>> = dao.getTrashed()
    fun project(id: Long) = dao.observeById(id)

    suspend fun add(name: String, description: String? = null): Long =
        dao.insert(ProjectEntity(name = name, description = description))

    suspend fun getByName(name: String): ProjectEntity? = dao.findByName(name)

    suspend fun rename(id: Long, name: String, description: String?) =
        dao.update(id = id, name = name, description = description, updatedAt = System.currentTimeMillis())

    suspend fun setDone(id: Long, done: Boolean) =
        dao.setDone(id, done, System.currentTimeMillis())

    /** Soft delete → Papierkorb */
    suspend fun moveToTrash(id: Long) {
        val ts = System.currentTimeMillis()
        // abhängige Einträge ebenfalls in den Papierkorb
        noteDao.markTrashedByProject(id, ts)
        todoDao.markTrashedByProject(id, ts)
        dao.markTrashed(id, ts)
    }

    /** Wiederherstellen aus Papierkorb */
    suspend fun restoreFromTrash(id: Long) {
        val ts = System.currentTimeMillis()
        dao.restore(id, ts)
        noteDao.restoreByProject(id)
        todoDao.restoreByProject(id)
    }

    /** Endgültig löschen (inkl. abhängiger Daten, um FK-RESTRICT zu vermeiden) */
    suspend fun hardDelete(id: Long) {
        todoDao.hardDeleteByProject(id)
        noteDao.hardDeleteByProject(id)
        dao.deleteById(id)
    }
}
