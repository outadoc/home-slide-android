package fr.outadoc.homeslide.app.overlay.ui

import androidx.ui.tooling.preview.PreviewParameterProvider
import fr.outadoc.homeslide.hassapi.model.AttributeSet
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.MaterialIconLocator
import fr.outadoc.mdi.MaterialIconMapper

class EntityStateParameterProvider : PreviewParameterProvider<EntityState> {
    override val values: Sequence<EntityState>
        get() {
            MaterialIconLocator.instance = PreviewMaterialIconMapper
            return sequenceOf(
                EntityState(
                    entityId = "light.sample",
                    state = "on",
                    attributes = AttributeSet(
                        friendlyName = "Sample Light",
                        icon = "mdi:lightbulb",
                        brightness = 200f,
                        supportedFeatures = 1
                    )
                )
            )
        }
}

object PreviewMaterialIconMapper : MaterialIconMapper {

    override fun getIcon(iconName: String) =
        FontIcon("\uDB80\uDF35")
}