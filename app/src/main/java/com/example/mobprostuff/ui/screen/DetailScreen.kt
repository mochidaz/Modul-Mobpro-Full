package com.example.mobprostuff.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobprostuff.viewmodels.DetailViewModel
import com.example.mobprostuff.R
import com.example.mobprostuff.database.Student
import com.example.mobprostuff.database.StudentDB
import com.example.mobprostuff.types.Action
import com.example.mobprostuff.utils.ViewModelFactory

const val KEY_STUDENT_ID = "studentId"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController, id: String? = null) {
    val context = LocalContext.current
    val db = StudentDB.getInstance(context)
    val factory = ViewModelFactory(db.dao)
    val viewModel: DetailViewModel = viewModel(factory = factory)

    var studentId by remember { mutableStateOf(id) }
    var name by remember { mutableStateOf("") }
    var _class by remember { mutableStateOf("") }

    var action by remember { mutableStateOf(Action.ADD) }

    LaunchedEffect(true) {
        if (studentId == null) {
            return@LaunchedEffect
        }

        val student = viewModel.getStudentById(studentId!!) ?: return@LaunchedEffect

        name = student.name
        _class = student._class

        action = Action.EDIT
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                title = {
                    Text(
                        text = if (studentId == null) stringResource(id = R.string.add_student)
                        else stringResource(id = R.string.edit_student)
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {
                        if (name.isBlank() || _class.isBlank()) {
                            Toast.makeText(context, R.string.invalid, Toast.LENGTH_LONG).show()
                            return@IconButton
                        }
                        val student = Student(studentId ?: "", name, _class)
                        if (action == Action.ADD) {
                            viewModel.insertStudent(student)
                        } else {
                            viewModel.updateStudent(student)
                        }
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = stringResource(id = R.string.save),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    studentId?.let {
                        DeleteAction {
                            viewModel.deleteStudent(it)
                            navController.popBackStack()
                        }
                    }
                }
            )
        }
    ) { padding ->
        StudentForm(
            id = studentId.orEmpty(),
            onIdChange = { studentId = it },
            name = name,
            onNameChange = { name = it },
            selectedOption = _class,
            onOptionSelected = { _class = it },
            action = action,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun StudentForm(
    id: String, onIdChange: (String) -> Unit,
    name: String, onNameChange: (String) -> Unit,
    selectedOption: String, onOptionSelected: (String) -> Unit,
    action: Action,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = id,
            onValueChange = onIdChange,
            label = { Text(text = stringResource(id = R.string.label_id)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            enabled = action == Action.ADD
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(text = stringResource(id = R.string.label_name)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = stringResource(id = R.string.label_class))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("D3IF-47-01", "D3IF-47-02", "D3IF-47-03", "D3IF-47-04").forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == selectedOption),
                            onClick = { onOptionSelected(option) }
                        )
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = { onOptionSelected(option) }
                    )
                    Text(text = option)
                }
            }
        }
    }
}

@Composable
fun DeleteAction(delete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = stringResource(id = R.string.other),
            tint = MaterialTheme.colorScheme.primary
        )

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.delete))
                },
                onClick = {
                    expanded = false
                    delete()
                },
            )
        }
    }
}
