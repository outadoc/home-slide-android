package fr.outadoc.quickhass.model

data class Action(
    val domain: String,
    val service: String,
    val entity: Entity,
    val additionalParams: Map<String, String> = emptyMap()
)