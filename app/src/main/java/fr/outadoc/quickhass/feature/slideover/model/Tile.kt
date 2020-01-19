package fr.outadoc.quickhass.feature.slideover.model

data class Tile<T>(
    val source: T,
    val isActivated: Boolean,
    val label: String,
    val icon: String?,
    val state: String?,
    val isLoading: Boolean,
    val isHidden: Boolean
)