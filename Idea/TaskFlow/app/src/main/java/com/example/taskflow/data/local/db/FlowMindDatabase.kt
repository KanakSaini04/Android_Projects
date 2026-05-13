package com.example.taskflow.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskflow.data.local.dao.NoteDao
import com.example.taskflow.data.local.dao.TaskDao
import com.example.taskflow.data.local.entity.Note
import com.example.taskflow.data.local.entity.NoteImage
import com.example.taskflow.data.local.entity.Task
import com.example.taskflow.data.local.dao.NoteImageDao

@Database(
    entities = [Task::class, Note::class, NoteImage::class],
    version = 2,
    exportSchema = false
)
abstract class FlowMindDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun noteImageDao(): NoteImageDao

    companion object {
        @Volatile
        private var INSTANCE: FlowMindDatabase? = null

        fun getDatabase(context: Context): FlowMindDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlowMindDatabase::class.java,
                    "taskflow_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}