package com.example.thepsychologist.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class ChatDataSource (context: Context) {
    private val dbHelper: ChatDatabaseHelper = ChatDatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
    }

    fun addMessage(userId: Int, message: String) {
        val values = ContentValues().apply {
            put("user_id", userId)
            put("message", message)
        }
        database.insert("chat", null, values)
    }

    fun getAllMessages(): List<Pair<Int, String>> {
        val messages = mutableListOf<Pair<Int, String>>()
        val cursor: Cursor = database.query(
            "chat", null, null, null, null, null,
            "timestamp DESC"
        )
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val message = cursor.getString(cursor.getColumnIndex("message"))
            val id = cursor.getInt(cursor.getColumnIndex("user_id"))
            messages.add(Pair(id, message))
            cursor.moveToNext()
        }
        cursor.close()
        return messages
    }
}