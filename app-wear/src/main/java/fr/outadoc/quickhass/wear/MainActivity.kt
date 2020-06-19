package fr.outadoc.quickhass.wear

import android.os.Bundle
import fr.outadoc.homeslide.common.DayNightActivity
import fr.outadoc.homeslide.common.ThemeProvider
import fr.outadoc.quickhass.wear.feature.list.EntityListFragment

class MainActivity : DayNightActivity() {

    override val themeProvider: ThemeProvider = object : ThemeProvider {
        override val preferredTheme: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_wearActivity_content, EntityListFragment.newInstance())
            .commit()
    }
}
