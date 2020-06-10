package fr.outadoc.homeslide.common.preferences

import fr.outadoc.homeslide.rest.baseurl.PreferredBaseUrl

interface UrlPreferenceRepository {

    var instanceBaseUrl: String?
    var altInstanceBaseUrl: String?
    var preferredBaseUrl: PreferredBaseUrl
}