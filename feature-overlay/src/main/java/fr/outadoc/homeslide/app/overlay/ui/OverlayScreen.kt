package fr.outadoc.homeslide.app.overlay.ui

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import fr.outadoc.homeslide.hassapi.factory.EntityFactory
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.Light

@Preview(name = "Control overlay", showDecoration = true)
@Composable
fun ControlOverlayScreenPreview(
    @PreviewParameter(EntityStateParameterProvider::class)
    entityState: EntityState
) {
    ControlOverlayScreen(entityState = entityState)
}

@Composable
fun ControlOverlayScreen(entityState: EntityState) {
    Box(
        backgroundColor = Color.Black.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxSize(),
        gravity = ContentGravity.Center
    ) {
        ControlOverlay(entityState = entityState)
    }
}

@Composable
fun ControlOverlay(entityState: EntityState) {
    when (val entity = EntityFactory.create(entityState)) {
        is Light -> LightControlOverlay(light = entity)
    }
}

@Composable
fun LightControlOverlay(light: Light) {
    light.brightness?.let { brightness ->
        VerticalSlider(
            value = brightness,
            modifier = Modifier.fillMaxHeight(0.6f),
            onValueChange = {
                // update brightness
            }
        )
    }
}