package com.dominikpetrich.hubideas.ui.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ProjectDetailTabs(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val titles = listOf("To-Dos", "Notizen")
    TabRow(selectedTabIndex = selected) {
        titles.forEachIndexed { i, title ->
            Tab(
                selected = selected == i,
                onClick = { onSelected(i) },
                text = { Text(title) }
            )
        }
    }
}
