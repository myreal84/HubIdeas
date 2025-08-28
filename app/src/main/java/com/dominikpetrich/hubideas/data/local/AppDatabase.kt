package com.dominikpetrich.hubideas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dominikpetrich.hubideas.data.local.dao.NoteDao
import com.dominikpetrich.hubideas.data.local.dao.ProjectDao
import com.dominikpetrich.hubideas.data.local.dao.TodoDao
import com.dominikpetrich.hubideas.data.local.entity.NoteEntity
import com.dominikpetrich.hubideas.data.local.entity.ProjectEntity
import com.dominikpetrich.hubideas.data.local.entity.TodoEntity

@Database(
    entities = [NoteEntity::class, ProjectEntity::class, TodoEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun projectDao(): ProjectDao
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `projects` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `description` TEXT,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER,
                        `archivedAt` INTEGER
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_projects_name ON projects(name)")
                db.execSQL("INSERT OR IGNORE INTO projects (id, name, description, createdAt, updatedAt, archivedAt) VALUES (1, 'Inbox', NULL, strftime('%s','now')*1000, NULL, NULL)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `notes_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `content` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `projectId` INTEGER NOT NULL DEFAULT 1,
                        FOREIGN KEY(`projectId`) REFERENCES `projects`(`id`) ON UPDATE CASCADE ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )
                db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='notes'").use { c ->
                    if (c.moveToFirst()) {
                        db.execSQL("INSERT INTO notes_new (id, content, createdAt, projectId) SELECT id, content, createdAt, 1 FROM notes")
                        db.execSQL("DROP TABLE IF EXISTS notes")
                    }
                }
                db.execSQL("ALTER TABLE notes_new RENAME TO notes")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_projectId ON notes(projectId)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE projects ADD COLUMN isDone INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `todos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `projectId` INTEGER NOT NULL,
                        `title` TEXT NOT NULL,
                        `isDone` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL,
                        FOREIGN KEY(`projectId`) REFERENCES `projects`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_projectId ON todos(projectId)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE projects ADD COLUMN trashedAt INTEGER")
                db.execSQL("ALTER TABLE notes ADD COLUMN trashedAt INTEGER")
                db.execSQL("ALTER TABLE todos ADD COLUMN trashedAt INTEGER")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hubideas.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("INSERT OR IGNORE INTO projects (id, name, description, createdAt, updatedAt, archivedAt) VALUES (1, 'Inbox', NULL, strftime('%s','now')*1000, NULL, NULL)")
                    }
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        // Auto-Purge: alles löschen, was länger als 30 Tage im Papierkorb liegt
                        val tsExpr = "(strftime('%s','now') - 30*24*60*60) * 1000"
                        db.execSQL("DELETE FROM todos WHERE trashedAt IS NOT NULL AND trashedAt < $tsExpr")
                        db.execSQL("DELETE FROM notes WHERE trashedAt IS NOT NULL AND trashedAt < $tsExpr")
                        db.execSQL("DELETE FROM projects WHERE trashedAt IS NOT NULL AND trashedAt < $tsExpr")
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
    }
}
