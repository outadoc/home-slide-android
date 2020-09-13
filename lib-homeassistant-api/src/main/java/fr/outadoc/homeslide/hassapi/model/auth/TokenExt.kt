package fr.outadoc.homeslide.hassapi.model.auth

import kotlin.time.DurationUnit
import kotlin.time.toDuration

val Token.expiresInDuration
    get() = expiresIn.toDuration(DurationUnit.SECONDS)
