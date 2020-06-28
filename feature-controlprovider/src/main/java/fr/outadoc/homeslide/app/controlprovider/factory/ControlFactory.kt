package fr.outadoc.homeslide.app.controlprovider.factory

import android.os.Build
import android.service.controls.Control
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity

@RequiresApi(Build.VERSION_CODES.R)
interface ControlFactory {
    fun createControl(entity: Entity): Control?
    fun createStatefulControl(entity: Entity): Control?
}
