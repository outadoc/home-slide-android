package fr.outadoc.homeslide.rest

interface AltBaseUrlInterceptorConfigProvider {
    val instanceBaseUrl: String
    val altInstanceBaseUrl: String?
    var preferredBaseUrl: PreferredBaseUrl
}