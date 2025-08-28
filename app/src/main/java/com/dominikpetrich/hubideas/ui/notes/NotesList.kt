package com.dominikpetrich.hubideas.ui.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dominikpetrich.hubideas.data.local.entity.NoteEntity
import java.text.DateFormat
import java.util.Date

@Composable
fun NotesList(
    notes: List<NoteEntity>,
    onNoteClick: (NoteEntity) -> Unit = {}
) {
    if (notes.isEmpty()) {
        Text("Keine Notizen", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn {
            items(notes, key = { it.id }) { n ->
                ListItem(
                    headlineContent = { Text(n.content) },
                    supportingContent = {
                        Text(DateFormat.getDateTimeInstance().format(Date(n.createdAt)))
                    },
                    modifier = Modifier.clickable { onNoteClick(n) }
                )
                Divider()
            }
        }
    }
}
