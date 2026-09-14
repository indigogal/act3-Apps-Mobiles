package com.github.indigogal.act3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.indigogal.act3.ui.theme.AppTheme
import com.github.indigogal.act3.ui.theme.AppTypography
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: LocalDateTime
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()
}

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: android.content.Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

data class ReminderVMState(
    var reminders: List<Note>? = null
)

class ReminderVM(private val noteDao: NoteDao) : ViewModel() {
    private val _uiState = MutableStateFlow(ReminderVMState())
    val uiState: StateFlow<ReminderVMState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            noteDao.getAll().collect { notes ->
                _uiState.update { currentState ->
                    currentState.copy(reminders = notes)
                }
            }
        }
    }

    fun addReminder(newNote: Note) {
        viewModelScope.launch {
            noteDao.insert(newNote)
        }
    }

    fun delAllReminders() {
        viewModelScope.launch {
            noteDao.deleteAll()
        }
    }

    fun delReminder(id: Long) {
        viewModelScope.launch {
            noteDao.deleteById(id)
        }
    }
}

class ReminderVMFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderVM(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun ReminderCard(data: Note, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        shape = CardDefaults.elevatedShape
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "·${data.title}",
                style = AppTypography.displaySmall,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = data.content,
                style = AppTypography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = data.date.toLocalDate().toString(),
                style = AppTypography.headlineSmall,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderCardPreview() {
    val data = Note(
        id = 0,
        title = "Test",
        content = "Lorem Ipsum",
        date = LocalDateTime.now(),
    )
    AppTheme {
        ReminderCard(data)
    }
}
