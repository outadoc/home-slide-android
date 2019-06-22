package fr.outadoc.quickhass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.outadoc.quickhass.model.Shortcut

class QuickAccessViewModel : ViewModel() {

    private val _shortcuts: MutableLiveData<List<Shortcut>> by lazy {
        MutableLiveData<List<Shortcut>>().apply {
            value = listOf(
                Shortcut("Séjour"),
                Shortcut("Chambre"),
                Shortcut("Cuisine")
            )
        }
    }
    val shortcuts: LiveData<List<Shortcut>>
        get() = _shortcuts
}