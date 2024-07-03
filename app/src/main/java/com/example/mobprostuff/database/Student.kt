package com.example.mobprostuff.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class Student(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "class")
    val _class: String,
)