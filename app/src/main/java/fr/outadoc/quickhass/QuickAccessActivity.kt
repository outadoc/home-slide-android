package fr.outadoc.quickhass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.view.Gravity
import android.view.WindowManager


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
