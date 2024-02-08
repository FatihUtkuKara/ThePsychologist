package com.example.thepsychologist.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ChatDatabaseHelper(context : Context) : SQLiteOpenHelper(context,DATABASE_NAME,null ,DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "chat_database"
        private const val DATABASE_VERSION = 1

        // Tablo oluşturma sorgusu
        private const val CREATE_TABLE_CHAT = "CREATE TABLE chat (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "message TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_CHAT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS chat")
        onCreate(db)
    }
}