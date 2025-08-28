package com.dominikpetrich.hubideas.data.repo

import com.dominikpetrich.hubideas.data.local.dao.TodoDao
import com.dominikpetrich.hubideas.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val dao: TodoDao) {
    fun todosOf(projectId: Long): Flow<List<TodoEntity>> = dao.getByProject(projectId)
    suspend fun add(projectId: Long, title: String) = dao.insert(TodoEntity(projectId = projectId, title = title))
    suspend fun setDone(id: Long, done: Boolean) = dao.setDone(id, done)
    suspend fun delete(id: Long) = dao.deleteById(id)
}
