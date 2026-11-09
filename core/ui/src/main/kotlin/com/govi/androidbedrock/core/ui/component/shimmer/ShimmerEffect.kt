package com.govi.androidbedrock.core.ui.component.shimmer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.govi.androidbedrock.core.ui.theme.ShimmerDark
import com.govi.androidbedrock.core.ui.theme.ShimmerHighlightDark
import com.govi.androidbedrock.core.ui.theme.ShimmerHighlightLight
import com.govi.androidbedrock.core.ui.theme.ShimmerLight

/**
 * Modifier that applies an animated linear gradient shimmer placeholder to any Composable.
 */
fun Modifier.shimmer(): Modifier =
    composed {
        val isDark = isSystemInDarkTheme()
        val baseColor = if (isDark) ShimmerDark else ShimmerLight
        val highlightColor = if (isDark) ShimmerHighlightDark else ShimmerHighlightLight

        val transition = rememberInfiniteTransition(label = "ShimmerTransition")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "ShimmerTranslate",
        )

        val brush =
            Brush.linearGradient(
                colors = listOf(baseColor, highlightColor, baseColor),
                start = Offset(translateAnim - 400f, translateAnim - 400f),
                end = Offset(translateAnim, translateAnim),
            )

        this.background(brush)
    }

/**
 * Reusable placeholder box with rounded corners and animated shimmer effect.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier =
            modifier
                .clip(shape)
                .shimmer(),
    )
}

/**
 * Skeleton placeholder item mimicking a standard card with avatar and text lines.
 */
@Composable
fun ShimmerListItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBox(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                ShimmerBox(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height(16.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                ShimmerBox(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.9f)
                            .height(12.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerListItemPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ShimmerListItem()
            ShimmerListItem()
            ShimmerListItem()
        }
    }
}
