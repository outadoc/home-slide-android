package fr.outadoc.homeslide.common.feature.auth.repository

import fr.outadoc.homeslide.rest.auth.OAuthConfiguration

class AppOAuthConfiguration : OAuthConfiguration {

    override val clientId: String = "https://homeslide.app"
    override val redirectUri: String = "homeslide://auth_callback"
}
