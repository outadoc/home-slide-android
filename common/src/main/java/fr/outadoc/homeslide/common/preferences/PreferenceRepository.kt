package fr.outadoc.homeslide.common.preferences

import fr.outadoc.homeslide.rest.baseurl.PreferredBaseUrl

interface PreferenceRepository {

    var instanceBaseUrl: String
    var altInstanceBaseUrl: String?
    var preferredBaseUrl: PreferredBaseUrl

    var accessToken: String?
    var refreshToken: String?

    val refreshIntervalSeconds: Long
    var showWhenLocked: Boolean
    val theme: String

    var isOnboardingDone: Boolean
}