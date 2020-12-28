package enyciaa.composelist.playground

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp

fun paddingValues(
    horizontalPadding: Dp,
    verticalPadding: Dp,
): PaddingValues {
    return PaddingValues(
        start = horizontalPadding,
        end = horizontalPadding,
        top = verticalPadding,
        bottom = verticalPadding
    )
}