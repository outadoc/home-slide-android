package fr.outadoc.homeslide.app.controlprovider.factory

import android.content.Context
import android.os.Build
import android.service.controls.Control
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity

@RequiresApi(Build.VERSION_CODES.R)
interface ControlFactory {
    fun createControl(context: Context, entity: Entity): Control?
    fun createStatefulControl(context: Context, entity: Entity): Control?
}
