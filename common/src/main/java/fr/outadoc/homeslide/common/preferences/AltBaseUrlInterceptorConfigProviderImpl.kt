package fr.outadoc.homeslide.common.preferences

import fr.outadoc.homeslide.rest.AltBaseUrlInterceptorConfigProvider
import fr.outadoc.homeslide.rest.PreferredBaseUrl

class AltBaseUrlInterceptorConfigProviderImpl(private val prefs: PreferenceRepository) :
    AltBaseUrlInterceptorConfigProvider {

    override val instanceBaseUrl: String
        get() = prefs.instanceBaseUrl

    override val altInstanceBaseUrl: String?
        get() = prefs.altInstanceBaseUrl

    override var preferredBaseUrl: PreferredBaseUrl
        get() = prefs.preferredBaseUrl
        set(value) {
            prefs.preferredBaseUrl = value
        }
}