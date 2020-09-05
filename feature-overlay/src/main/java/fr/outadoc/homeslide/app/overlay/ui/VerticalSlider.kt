package fr.outadoc.homeslide.app.overlay.ui

import androidx.compose.foundation.layout.Stack
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderConstants
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawLayer
import androidx.compose.ui.graphics.Color
import androidx.ui.tooling.preview.Preview

@Preview
@Composable
fun VerticalSliderPreview() {
    VerticalSlider(value = 0.5f, onValueChange = {})
}

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeEnd: () -> Unit = {},
    thumbColor: Color = MaterialTheme.colors.primary,
    activeTrackColor: Color = MaterialTheme.colors.primary,
    inactiveTrackColor: Color = activeTrackColor.copy(alpha = SliderConstants.InactiveTrackColorAlpha),
    activeTickColor: Color = MaterialTheme.colors.onPrimary.copy(alpha = SliderConstants.TickColorAlpha),
    inactiveTickColor: Color = activeTrackColor.copy(alpha = SliderConstants.TickColorAlpha)
) {
    Rotate(rotation = 90f) {
        Slider(
            value,
            onValueChange,
            modifier,
            valueRange,
            steps,
            onValueChangeEnd,
            thumbColor,
            activeTrackColor,
            inactiveTrackColor,
            activeTickColor,
            inactiveTickColor
        )
    }
}

@Composable
fun Rotate(rotation: Float, children: @Composable () -> Unit) {
    Stack(
        modifier = Modifier.drawLayer(
            rotationX = rotation,
            rotationY = rotation
        )
    ) {
        children()
    }
}