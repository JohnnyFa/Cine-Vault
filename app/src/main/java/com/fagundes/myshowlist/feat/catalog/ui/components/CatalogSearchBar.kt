package com.fagundes.myshowlist.feat.catalog.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.ui.theme.Divider
import com.fagundes.myshowlist.ui.theme.Surface
import com.fagundes.myshowlist.ui.theme.TextMuted
import com.fagundes.myshowlist.ui.theme.TextSecondary

@Composable
fun CatalogSearchBar(
    value: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onSearchChange,
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp),
        placeholder = {
            Text(
                text = "Search movies",
                color = TextMuted,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextMuted,
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                disabledContainerColor = Surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Divider,
                cursorColor = TextSecondary,
            ),
    )
}
