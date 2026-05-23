package com.fagundes.myshowlist.feat.login.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.ui.theme.AccentGold
import com.fagundes.myshowlist.ui.theme.TextMuted

@Composable
fun TermsAndPrivacyText() {
    val annotatedText =
        buildAnnotatedString {
            append(stringResource(R.string.by_continuing))

            pushLink(
                LinkAnnotation.Clickable(
                    tag = stringResource(R.string.terms),
                    linkInteractionListener =
                        LinkInteractionListener {
                            // sem ação por enquanto
                        },
                ),
            )
            withStyle(
                SpanStyle(
                    color = AccentGold,
                    fontWeight = FontWeight.Medium,
                ),
            ) {
                append(stringResource(R.string.terms))
            }
            pop()

            append(stringResource(R.string.and))

            pushLink(
                LinkAnnotation.Clickable(
                    tag = stringResource(R.string.privacy),
                    linkInteractionListener =
                        LinkInteractionListener {
                            // sem ação por enquanto
                        },
                ),
            )

            withStyle(
                SpanStyle(
                    color = AccentGold,
                    fontWeight = FontWeight.Medium,
                ),
            ) {
                append(stringResource(R.string.privacy))
            }
            pop()
        }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodySmall,
        color = TextMuted,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}
