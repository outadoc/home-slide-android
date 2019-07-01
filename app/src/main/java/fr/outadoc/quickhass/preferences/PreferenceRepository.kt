package fr.outadoc.quickhass.preferences

interface PreferenceRepository {

    val instanceBaseUrl: String
    val accessToken: String
    val theme: String

    val shouldAskForInitialValues: Boolean
}