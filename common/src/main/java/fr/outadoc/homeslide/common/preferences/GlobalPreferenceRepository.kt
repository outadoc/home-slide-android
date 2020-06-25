package fr.outadoc.homeslide.common.preferences

interface GlobalPreferenceRepository {

    var isOnboardingDone: Boolean
    val refreshIntervalSeconds: Long
    var showWhenLocked: Boolean
    val theme: String
}
