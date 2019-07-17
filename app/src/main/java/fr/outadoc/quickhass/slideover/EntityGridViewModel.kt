package fr.outadoc.quickhass.slideover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.Entity
import fr.outadoc.quickhass.persistence.EntityDatabase
import fr.outadoc.quickhass.persistence.model.PersistedEntity
import fr.outadoc.quickhass.preferences.PreferenceRepositoryImpl
import fr.outadoc.quickhass.rest.EntityRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EntityGridViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceRepositoryImpl(application.applicationContext)
    private val repository = EntityRepositoryImpl(application.applicationContext, prefs)

    private val _result = MutableLiveData<Result<List<Entity>>>()
    val result: LiveData<Result<List<Entity>>> = _result

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _shouldAskForInitialValues = MutableLiveData<Boolean>()
    val shouldAskForInitialValues: LiveData<Boolean> = _shouldAskForInitialValues

    private val _isEditingMode = MutableLiveData(false)
    val isEditingMode: LiveData<Boolean> = _isEditingMode

    private val db = Room.databaseBuilder(
        application.applicationContext,
        EntityDatabase::class.java, EntityDatabase.DB_NAME
    ).build()

    fun loadShortcuts() {
        if (prefs.isOnboardingDone) {
            _shouldAskForInitialValues.value = prefs.isOnboardingDone
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            val res = repository.getEntities()
            _result.postValue(res)

            _isLoading.postValue(false)
        }
    }

    fun onEntityClick(item: Entity) {
        if (item.primaryAction == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            repository.callService(item.primaryAction as Action)
                .onSuccess {
                    loadShortcuts()
                }.onFailure {
                    _result.postValue(Result.failure(it))
                }

            _isLoading.postValue(false)
        }
    }

    fun onReorderedEntities(items: List<Entity>) {
        if (items.isEmpty()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val toBePersisted = items.mapIndexed { idx, item ->
                PersistedEntity(item.entityId, idx)
            }

            // Update database
            with(db.entityDao()) {
                replaceAll(toBePersisted)
            }
        }
    }

    fun onEditClick() {
        _isEditingMode.value = _isEditingMode.value != true
    }
}