package com.github.indigogal.act3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.lifecycle.ViewModel
import com.github.indigogal.act3.ui.theme.AppTheme
import com.github.indigogal.act3.ui.theme.AppTypography
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Reminder(
    val id: Int,
    val name: String,
    val content: String,
    val dueBy: LocalDate
)
data class ReminderVMState(
    var reminders: List<Reminder>? = null
)

class ReminderVM : ViewModel(){
    private val _uiState = MutableStateFlow(ReminderVMState())
    val uiState: StateFlow<ReminderVMState> = _uiState.asStateFlow()

    fun addReminder(newReminder: Reminder){
        _uiState.update { currentState ->
            val updatedReminders = currentState.reminders.orEmpty() + newReminder
            ReminderVMState(reminders = updatedReminders)
        }
    }

    fun delAllReminders(){
        _uiState.update { currentState ->
            ReminderVMState(reminders = listOf<Reminder>())
        }
    }

    fun delReminder(id: Int){
        val currentReminders = _uiState.value.reminders.orEmpty()
        _uiState.value = ReminderVMState(
            reminders = currentReminders.filterNot { it.id == id }
        )
    }
}

@Composable
fun ReminderCard(data: Reminder, modifier: Modifier = Modifier){
    ElevatedCard(
        modifier = modifier.size(width = 250.dp, height = 220.dp).padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(16.dp),
        shape = CardDefaults.elevatedShape
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
         Text(
             text = "${data.id}·${data.name}",
             style = AppTypography.displaySmall,
         )
             Spacer(Modifier.height(16.dp))
             Text(
                 text = data.content,
                 style = AppTypography.bodyMedium
             )
             Spacer(Modifier.height(16.dp))
             Text(
                 text = data.dueBy.toString(),
                 style = AppTypography.headlineSmall,
                 fontStyle = FontStyle.Italic
             )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderCardPreview(){
    val data: Reminder = Reminder(
        id = 0,
        name = "Test",
        content = "Lorem Ipsum",
        dueBy = LocalDate.now()
    )
    AppTheme() {
        ReminderCard(data)
    }
}