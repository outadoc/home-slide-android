package fr.outadoc.homeslide.app.controlprovider.repository

import android.os.Build
import android.service.controls.Control
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.hassapi.model.annotation.StringEntityId

@RequiresApi(Build.VERSION_CODES.R)
interface ControlRepository {
    suspend fun getEntities(): Result<List<Control>>
    suspend fun toggleEntity(@StringEntityId entityId: String)
    suspend fun getEntitiesWithState(): Result<List<Control>>
}
