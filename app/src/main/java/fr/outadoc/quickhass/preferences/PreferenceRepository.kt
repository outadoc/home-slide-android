package fr.outadoc.quickhass.preferences

interface PreferenceRepository {

    var instanceBaseUrl: String
    val altInstanceBaseUrl: String?
    var accessToken: String
    val theme: String

    var isOnboardingDone: Boolean
}