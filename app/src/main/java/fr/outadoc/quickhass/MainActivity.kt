package fr.outadoc.quickhass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.outadoc.quickhass.preferences.AppPreferencesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, AppPreferencesFragment.newInstance())
            .commit()
    }

}
