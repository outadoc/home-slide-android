package fr.outadoc.homeslide.common.preferences

interface TokenPreferenceRepository {

    var accessToken: String?
    var refreshToken: String?
}
