package com.dominikpetrich.hubideas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.dominikpetrich.hubideas.ui.NoteViewModel
import com.dominikpetrich.hubideas.ui.NoteViewModelFactory

class MainActivity : ComponentActivity() {
    private val vm by viewModels<NoteViewModel> { NoteViewModelFactory(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HubideasApp(vm) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubideasApp(vm: NoteViewModel) {
    val DarkColors = darkColorScheme(
        primary = Color(0xFF00C853), onPrimary = Color(0xFF001408),
        secondary = Color(0xFF1DE9B6), onSecondary = Color(0xFF001410),
        background = Color(0xFF0E1116), onBackground = Color(0xFFE6EAEF),
        surface = Color(0xFF12151B), onSurface = Color(0xFFF1F4F8)
    )
    MaterialTheme(colorScheme = DarkColors) {
        Scaffold(containerColor = DarkColors.background, topBar = { CenterAlignedTopAppBar(title = { Text("ideahub") }) }) { inner ->
            Box(Modifier.fillMaxSize().padding(inner)) { NotesScreen(vm) }
        }
    }
}

@Composable
fun NotesScreen(vm: NoteViewModel) {
    var noteText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    val notes by vm.notes.collectAsState()
    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = 620.dp).fillMaxWidth()) {
            Surface(shape = MaterialTheme.shapes.large, tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
                    OutlinedTextField(
                        value = noteText, onValueChange = { noteText = it }, placeholder = { Text("Neue Notiz…") },
                        maxLines = 3, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { val t = noteText.text.trim(); if (t.isNotEmpty()) { vm.add(t); noteText = TextFieldValue("") } })
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledTonalButton(onClick = { val t = noteText.text.trim(); if (t.isNotEmpty()) { vm.add(t); noteText = TextFieldValue("") } }) { Text("Hinzufügen") }
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(6.dp))
            if (notes.isEmpty()) {
                Text("Noch keine Notizen. Füge oben eine hinzu.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            } else {
                LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(notes, key = { it.id }) { item ->
                        NoteRow(text = item.content, onRename = { newText -> vm.rename(item.id, newText) }, onRemove = { vm.delete(item.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun NoteRow(text: String, onRename: (String) -> Unit, onRemove: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showRename by remember { mutableStateOf(false) }
    var tempText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    Surface(shape = MaterialTheme.shapes.large, tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, modifier = Modifier.weight(1f))
            Box {
                IconButton(onClick = { menuExpanded = true }) { Text("⋮", style = MaterialTheme.typography.titleLarge) }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text("Umbenennen") }, onClick = { tempText = TextFieldValue(text); menuExpanded = false; showRename = true })
                    DropdownMenuItem(text = { Text("Löschen") }, onClick = { menuExpanded = false; onRemove() })
                }
            }
        }
    }
    if (showRename) {
        AlertDialog(
            onDismissRequest = { showRename = false },
            title = { Text("Notiz umbenennen") },
            text = { OutlinedTextField(value = tempText, onValueChange = { tempText = it }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { val t = tempText.text.trim(); if (t.isNotEmpty()) { onRename(t); showRename = false } }) { Text("Speichern") } },
            dismissButton = { TextButton(onClick = { showRename = false }) { Text("Abbrechen") } }
        )
    }
}
