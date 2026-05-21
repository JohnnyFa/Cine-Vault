package com.fagundes.myshowlist.feat.login.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.ui.theme.Divider
import com.fagundes.myshowlist.ui.theme.TextMuted

@Composable
fun OrDivider() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Divider,
            thickness = 1.dp,
        )

        Text(
            text = stringResource(R.string.or),
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Divider,
            thickness = 1.dp,
        )
    }
}
