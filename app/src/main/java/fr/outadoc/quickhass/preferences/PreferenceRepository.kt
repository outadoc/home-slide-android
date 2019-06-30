package fr.outadoc.quickhass.preferences

interface PreferenceRepository {

    val instanceBaseUrl: String?
    val accessToken: String?
}