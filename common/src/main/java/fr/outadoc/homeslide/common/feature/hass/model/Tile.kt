package fr.outadoc.homeslide.common.feature.hass.model

data class Tile<T>(
    val source: T,
    val isActivated: Boolean,
    val isToggleable: Boolean,
    val label: String,
    val icon: String?,
    val state: String?,
    val isLoading: Boolean,
    val isHidden: Boolean
)