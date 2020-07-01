package fr.outadoc.homeslide.common.preferences

import java.time.Instant

interface TokenPreferenceRepository {

    var accessToken: String?
    var refreshToken: String?
    var tokenExpirationTime: Instant?
}
