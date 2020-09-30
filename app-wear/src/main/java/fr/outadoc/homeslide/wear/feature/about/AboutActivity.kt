package fr.outadoc.homeslide.wear.feature.about

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class AboutActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager
            .beginTransaction()
            .replace(android.R.id.content, AboutFragment.newInstance())
            .commit()
    }
}