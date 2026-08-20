package com.jrprofessor.serenity.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.ui.theme.AccentMint
import com.jrprofessor.serenity.ui.theme.TextPrimary

@Composable
fun ThemeBulletList(
    themes: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "You seem to be experiencing:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        themes.forEach { theme ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Ring icon bullet (like in Stitch design)
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .border(3.dp, AccentMint, CircleShape)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = theme,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}
