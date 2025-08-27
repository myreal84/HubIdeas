package com.dominikpetrich.hubideas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dominikpetrich.hubideas.data.local.dao.NoteDao
import com.dominikpetrich.hubideas.data.local.dao.ProjectDao
import com.dominikpetrich.hubideas.data.local.entity.NoteEntity
import com.dominikpetrich.hubideas.data.local.entity.ProjectEntity

@Database(
    entities = [NoteEntity::class, ProjectEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // Safe migration: create projects, seed Inbox, rebuild notes with FK; copy only if old notes table exists
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // projects table
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

                // seed Inbox(id=1) if missing
                db.execSQL(
                    "INSERT OR IGNORE INTO projects (id, name, description, createdAt, updatedAt, archivedAt) " +
                    "VALUES (1, 'Inbox', NULL, strftime('%s','now')*1000, NULL, NULL)"
                )

                // new notes table with FK
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

                // copy only if old notes exist
                db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='notes'").use { c ->
                    val hasOld = c.moveToFirst()
                    if (hasOld) {
                        db.execSQL("INSERT INTO notes_new (id, content, createdAt, projectId) SELECT id, content, createdAt, 1 FROM notes")
                        db.execSQL("DROP TABLE IF EXISTS notes")
                    }
                }

                db.execSQL("ALTER TABLE notes_new RENAME TO notes")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_projectId ON notes(projectId)")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hubideas.db"
                )
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        // Fresh create path: ensure Inbox exists
                        db.execSQL(
                            "INSERT OR IGNORE INTO projects (id, name, description, createdAt, updatedAt, archivedAt) " +
                            "VALUES (1, 'Inbox', NULL, strftime('%s','now')*1000, NULL, NULL)"
                        )
                    }
                })
                .fallbackToDestructiveMigration() // optional safety net for other future versions
                .build()
                .also { INSTANCE = it }
            }
    }
}
