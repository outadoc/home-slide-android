package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.State

class InputBooleanEntity(state: State) : BinaryEntity(state, "dip-switch".toIcon()!!)