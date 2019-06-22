package fr.outadoc.quickhass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.outadoc.quickhass.model.Shortcut
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue

class QuickAccessViewModel : ViewModel() {

    private val _shortcuts: MutableLiveData<List<Shortcut>> by lazy {
        MutableLiveData<List<Shortcut>>().apply {
            value = listOf(
                Shortcut("Séjour", IconValue.LIGHTBULB.name),
                Shortcut("Chambre", IconValue.LIGHTBULB.name),
                Shortcut("Cuisine", IconValue.LIGHTBULB.name),
                Shortcut("Couloir", IconValue.LIGHTBULB.name),
                Shortcut("Salle à manger", IconValue.LIGHTBULB.name),
                Shortcut("Salle de bain", IconValue.LIGHTBULB.name)
            )
        }
    }
    val shortcuts: LiveData<List<Shortcut>>
        get() = _shortcuts
}