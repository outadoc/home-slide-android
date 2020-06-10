package fr.outadoc.quickhass.wear

import android.os.Bundle
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import fr.outadoc.homeslide.common.DayNightActivity
import fr.outadoc.homeslide.common.ThemeProvider
import fr.outadoc.quickhass.wear.feature.list.EntityGridFragment
import fr.outadoc.quickhass.wear.preferences.PreferenceSyncViewModel
import org.koin.android.ext.android.inject

class MainActivity : DayNightActivity() {

    override val themeProvider: ThemeProvider = object : ThemeProvider {
        override val preferredTheme: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_wearActivity_content, EntityGridFragment.newInstance())
            .commit()
    }
}
