package fr.outadoc.homeslide.hassapi.model

import fr.outadoc.homeslide.hassapi.model.annotation.StringDomain
import fr.outadoc.homeslide.hassapi.model.annotation.StringEntityId

data class Action(
    @StringDomain
    val domain: String,
    val service: String,
    @StringEntityId
    val entityId: String,
    val additionalParams: Map<String, String> = emptyMap()
) {
    val allParams = mapOf("entity_id" to entityId) + additionalParams
}
