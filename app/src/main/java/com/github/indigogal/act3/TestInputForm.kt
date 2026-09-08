package com.github.indigogal.act3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.DateTimeException
import java.time.LocalDate
import androidx.compose.material3.TextButton
import com.github.indigogal.act3.ui.theme.AppTheme

@Composable
fun TestinputForm(
    viewModel: ReminderVM,
    onReminderSaved: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título del Recordatorio") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        OutlinedTextField(
            value = dueDate,
            onValueChange = { dueDate = it },
            label = { Text("Fecha Limite (DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )

        HorizontalDivider()

        Button(
            onClick = {
                val parsedDate = validateDate(dueDate)
                val formFilled = title.isNotBlank() && description.isNotBlank()
                if (formFilled && (parsedDate != null)) {
                    viewModel.addReminder(
                        Reminder(
                            id = System.currentTimeMillis().toInt(),
                            name = title,
                            content = description,
                            dueBy = parsedDate
                        )
                    )
                    title = ""
                    description = ""
                    dueDate = ""
                    onReminderSaved()
                } else if (parsedDate == null) {
                    dateError = "La fecha debe tener el formato DD/MM/YYYY y corresponder a una fecha válida."
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar Recordatorio")
        }

        HorizontalDivider()

    }

    dateError?.let { message ->
        ErrorModal(message = message) {
            dateError = null
        }
    }
}

@Composable
fun ErrorModal(
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}

fun validateDate(dateString: String): LocalDate? {
    val regex = Regex("""^(\d{2})/(\d{2})/(\d{4})$""")
    val matchResult = regex.find(dateString) ?: return null

    val (day, month, year) = matchResult.destructured
    return try {
        LocalDate.of(year.toInt(), month.toInt(), day.toInt())
    } catch (_: DateTimeException) {
        null
    }
}

@Preview
@Composable
fun ErrorModalPreview(){
    AppTheme {
        ErrorModal(message = "Error message") {}
    }
}

@Preview(showBackground = true)
@Composable
fun TestinputFormPreview() {
    TestinputForm(viewModel = viewModel()) {}
}