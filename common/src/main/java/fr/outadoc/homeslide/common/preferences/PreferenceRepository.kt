package fr.outadoc.homeslide.common.preferences

import fr.outadoc.homeslide.rest.PreferredBaseUrl

interface PreferenceRepository {

    var instanceBaseUrl: String
    var altInstanceBaseUrl: String?
    var accessToken: String
    val refreshIntervalSeconds: Long
    var showWhenLocked: Boolean
    val theme: String

    var isOnboardingDone: Boolean
    var preferredBaseUrl: PreferredBaseUrl
}