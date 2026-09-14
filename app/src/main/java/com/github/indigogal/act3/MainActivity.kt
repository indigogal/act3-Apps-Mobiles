package com.github.indigogal.act3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.indigogal.act3.ui.theme.AppTheme
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                ReminderApp()
            }
        }
    }
}

@Composable
fun ReminderApp() {
    val context = LocalContext.current
    val database = remember { NoteDatabase.getDatabase(context) }
    val viewModel: ReminderVM = viewModel(factory = ReminderVMFactory(database.noteDao()))
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "reminders") {
        composable("reminders") {
            ReminderScreen(viewModel = viewModel) {
                navController.navigate("add")
            }
        }
        composable("add") {
            TestinputForm(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun ReminderScreen(
    viewModel: ReminderVM,
    onAddReminder: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReminderContent(
        uiState = uiState,
        onAddReminder = onAddReminder,
        onDeleteReminder = { id -> viewModel.delReminder(id) }
    )
}

@Composable
fun ReminderContent(
    uiState: ReminderVMState,
    onAddReminder: () -> Unit,
    onDeleteReminder: (Long) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.reminders.orEmpty().isEmpty()) {
            Text(
                text = "No hay recordatorios todavía",
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(uiState.reminders.orEmpty(), key = { it.id }) { reminder ->
                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        isVisible = true
                    }

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                onDeleteReminder(reminder.id)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally() + fadeIn(),
                        exit = slideOutVertically()
                    ) {
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp)
                                        .background(Color.Red.copy(alpha = 0.8f))
                                )
                            },
                            content = {
                                ReminderCard(data = reminder)
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = onAddReminder,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Text("Nuevo Recordatorio")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderScreenPreview() {
    val dummyState = ReminderVMState(
        reminders = listOf(
            Note(1, "Comprar leche", "Ir al súper a por leche desnatada", LocalDateTime.now()),
            Note(2, "Estudiar Kotlin", "Terminar el ejercicio de Compose", LocalDateTime.now().plusDays(1))
        )
    )
    AppTheme {
        ReminderContent(uiState = dummyState, onAddReminder = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderScreenEmptyPreview() {
    AppTheme {
        ReminderContent(uiState = ReminderVMState(reminders = emptyList()), onAddReminder = {})
    }
}
