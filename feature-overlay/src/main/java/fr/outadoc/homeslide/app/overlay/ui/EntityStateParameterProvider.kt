package fr.outadoc.homeslide.app.overlay.ui

import androidx.ui.tooling.preview.PreviewParameterProvider
import fr.outadoc.homeslide.hassapi.model.AttributeSet
import fr.outadoc.homeslide.hassapi.model.EntityState

class EntityStateParameterProvider : PreviewParameterProvider<EntityState> {
    override val values: Sequence<EntityState>
        get() = sequenceOf(
            EntityState(
                entityId = "light.sample",
                state = "on",
                attributes = AttributeSet(
                    friendlyName = "Sample Light",
                    icon = "mdi:light"
                )
            )
        )
}