package fr.outadoc.quickhass.preferences

interface PreferenceRepository {

    var instanceBaseUrl: String
    var altInstanceBaseUrl: String?
    var accessToken: String
    val refreshIntervalSeconds: Long
    val showWhenLocked: Boolean
    val theme: String

    var isOnboardingDone: Boolean
    var preferredBaseUrl: PreferredBaseUrl
}