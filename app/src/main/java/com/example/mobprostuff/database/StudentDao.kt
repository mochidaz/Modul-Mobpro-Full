package com.example.mobprostuff.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert
    suspend fun insert(student: Student)

    @Update
    suspend fun update(student: Student)

    @Query("SELECT * FROM student WHERE id = :id")
    suspend fun getStudentById(id: String): Student?

    @Query("SELECT * FROM student ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("DELETE FROM student WHERE id = :id")
    suspend fun deleteStudent(id: String)
}