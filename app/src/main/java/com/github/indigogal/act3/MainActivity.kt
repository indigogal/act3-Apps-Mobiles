package com.github.indigogal.act3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.indigogal.act3.ui.theme.AppTheme

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
fun ReminderApp(viewModel: ReminderVM = viewModel()) {
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
                items(uiState.reminders.orEmpty()) { reminder ->
                    ReminderCard(data = reminder)
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