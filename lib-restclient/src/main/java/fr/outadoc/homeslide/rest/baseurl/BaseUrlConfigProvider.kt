package fr.outadoc.homeslide.rest.baseurl

interface BaseUrlConfigProvider {
    val instanceBaseUrl: String?
    val altInstanceBaseUrl: String?
    var preferredBaseUrl: PreferredBaseUrl
}