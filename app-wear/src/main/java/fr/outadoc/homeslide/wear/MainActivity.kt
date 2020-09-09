package fr.outadoc.homeslide.wear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.outadoc.homeslide.wear.feature.list.EntityListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_wearActivity_content, EntityListFragment.newInstance())
            .commit()
    }
}
