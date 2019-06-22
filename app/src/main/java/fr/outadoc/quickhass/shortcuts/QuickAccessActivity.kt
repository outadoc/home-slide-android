package fr.outadoc.quickhass.shortcuts

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity


class QuickAccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, QuickAccessFragment.newInstance())
            .commit()

        window.setGravity(Gravity.BOTTOM)
    }
}
