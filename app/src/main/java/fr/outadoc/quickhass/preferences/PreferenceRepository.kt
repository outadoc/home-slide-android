package fr.outadoc.quickhass.preferences

interface PreferenceRepository {

    val instanceBaseUrl: String
    val altInstanceBaseUrl: String?
    val accessToken: String
    val theme: String

    val isOnboardingDone: Boolean
}