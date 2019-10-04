package fr.outadoc.quickhass.preferences

interface PreferenceRepository {

    var instanceBaseUrl: String
    val altInstanceBaseUrl: String?
    var accessToken: String
    val refreshIntervalSeconds: Int
    val showWhenLocked: Boolean
    val theme: String

    var isOnboardingDone: Boolean
    var preferredBaseUrl: PreferredBaseUrl
}