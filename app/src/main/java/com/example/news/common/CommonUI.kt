package com.example.news.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.example.news.ui.theme.dimen0
import com.example.news.ui.theme.primaryTextColor

@Composable
fun CommonText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    textDecoration: TextDecoration = TextDecoration.None,
    textColor: Color = primaryTextColor,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimen0),
            verticalAlignment = Alignment.CenterVertically
        ) {

         Text(
                modifier = modifier,
                textAlign = textAlign,
                text = text,
                textDecoration = textDecoration,
                color = textColor,
                style = style,
                overflow = overflow,
                maxLines = maxLines
            )
        }
    }

