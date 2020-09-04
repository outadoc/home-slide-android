package fr.outadoc.homeslide.app.overlay.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import fr.outadoc.homeslide.hassapi.factory.EntityFactory
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.Light

@Preview
@Composable
fun ControlOverlay(
    @PreviewParameter(EntityStateParameterProvider::class)
    entityState: EntityState
) {
    when (val entity = EntityFactory.create(entityState)) {
        is Light -> LightControlOverlay(light = entity)
    }
}

@Composable
fun LightControlOverlay(light: Light) {
    Column {
        Switch(checked = light.isOn, onCheckedChange = {
            // toggle light
        })

        light.brightness?.let { brightness ->
            Slider(value = brightness.toFloat(), onValueChange = {
                // update brightness
            })
        }
    }
}