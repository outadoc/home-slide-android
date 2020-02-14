package fr.outadoc.quickhass.wear

import fr.outadoc.homeslide.common.preferences.PreferenceRepository
import fr.outadoc.homeslide.rest.baseurl.PreferredBaseUrl

class MockPreferenceRepository : PreferenceRepository {

    override var instanceBaseUrl: String?
        get() = "https://domos.outadoc.fr"
        set(value) {}

    override var altInstanceBaseUrl: String?
        get() = null
        set(value) {}

    override var preferredBaseUrl: PreferredBaseUrl
        get() = PreferredBaseUrl.PRIMARY
        set(value) {}

    override var accessToken: String?
        get() = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI4MzFkZWFmMjY5Nzc0YjdmODliYTNlMjJjMjVmOGUzMSIsImlhdCI6MTU2MzcyOTY3OCwiZXhwIjoxODc5MDg5Njc4fQ._YfiGRHLiCSquTiQTpWJ2RJeddttT2Pod1BNjCfJfPg"
        set(value) {}

    override var refreshToken: String?
        get() = null
        set(value) {}

    override val refreshIntervalSeconds: Long
        get() = 10

    override var showWhenLocked: Boolean
        get() = false
        set(value) {}

    override val theme: String
        get() = "day"

    override var isOnboardingDone: Boolean
        get() = true
        set(value) {}
}