package fr.outadoc.homeslide.common.preferences

import fr.outadoc.homeslide.rest.baseurl.BaseUrlConfigProvider
import fr.outadoc.homeslide.rest.baseurl.PreferredBaseUrl

class BaseUrlConfigProviderImpl(private val prefs: PreferenceRepository) :
    BaseUrlConfigProvider {

    override val instanceBaseUrl: String?
        get() = prefs.instanceBaseUrl

    override val altInstanceBaseUrl: String?
        get() = prefs.altInstanceBaseUrl

    override var preferredBaseUrl: PreferredBaseUrl
        get() = prefs.preferredBaseUrl
        set(value) {
            prefs.preferredBaseUrl = value
        }
}