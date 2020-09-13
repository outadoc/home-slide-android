package fr.outadoc.homeslide.common.preferences

import kotlinx.datetime.Instant

interface TokenPreferenceRepository {

    var accessToken: String?
    var refreshToken: String?
    var tokenExpirationTime: Instant?
}
