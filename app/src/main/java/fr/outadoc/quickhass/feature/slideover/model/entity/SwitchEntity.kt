package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.State

class SwitchEntity(state: State) : BinaryEntity(state, "power-plug".toIcon()!!)