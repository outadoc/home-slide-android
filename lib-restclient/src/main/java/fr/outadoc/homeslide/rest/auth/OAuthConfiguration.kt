package fr.outadoc.homeslide.rest.auth

interface OAuthConfiguration {
    val clientId: String
    val redirectUri: String
}
