package com.example.mobprostuff.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobprostuff.database.Student
import com.example.mobprostuff.database.StudentDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DetailViewModel(private val dao: StudentDao) : ViewModel() {
    fun insertStudent(student: Student) {
        val newStudent = Student(
            id = student.id,
            name = student.name,
            _class = student._class
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(newStudent)
        }
    }

    fun updateStudent(student: Student) {
        val updatedStudent = Student(
            id = student.id,
            name = student.name,
            _class = student._class
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.update(updatedStudent)
        }
    }

    suspend fun getStudentById(id: String): Student? {
        return dao.getStudentById(id)
    }

    fun getAllStudents(): Flow<List<Student>> {
        return dao.getAllStudents()
    }

    fun deleteStudent(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteStudent(id)
        }
    }
}