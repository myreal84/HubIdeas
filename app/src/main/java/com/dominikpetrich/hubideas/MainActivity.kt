package com.dominikpetrich.hubideas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HubideasApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubideasApp() {
    // Dark Material 3 Farbschema
    val DarkColors = darkColorScheme(
        primary = Color(0xFF00C853),
        onPrimary = Color(0xFF001408),
        secondary = Color(0xFF1DE9B6),
        onSecondary = Color(0xFF001410),
        background = Color(0xFF0E1116),
        onBackground = Color(0xFFE6EAEF),
        surface = Color(0xFF12151B),
        onSurface = Color(0xFFF1F4F8)
    )

    MaterialTheme(colorScheme = DarkColors) {
        Scaffold(
            containerColor = DarkColors.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("ideahub") }
                )
            }
        ) { inner ->
            Box(Modifier.fillMaxSize().padding(inner)) {
                NotesScreen()
            }
        }
    }
}

@Composable
fun NotesScreen() {
    var noteText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var notes by rememberSaveable { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 620.dp)
                .fillMaxWidth()
        ) {
            // Eingabe-Karte
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("Neue Notiz…") },
                        maxLines = 3,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            val t = noteText.text.trim()
                            if (t.isNotEmpty()) {
                                notes = notes + t
                                noteText = TextFieldValue("")
                            }
                        })
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledTonalButton(
                        onClick = {
                            val t = noteText.text.trim()
                            if (t.isNotEmpty()) {
                                notes = notes + t
                                noteText = TextFieldValue("")
                            }
                        }
                    ) { Text("Hinzufügen") }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(6.dp))

            if (notes.isEmpty()) {
                Text(
                    "Noch keine Notizen. Füge oben eine hinzu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(notes) { index, item ->
                        NoteRow(
                            text = item,
                            onRemove = {
                                notes = notes.toMutableList().also { it.removeAt(index) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteRow(text: String, onRemove: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, modifier = Modifier.weight(1f))
            OutlinedButton(onClick = onRemove) { Text("Löschen") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotes() {
    HubideasApp()
}
