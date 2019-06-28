package fr.outadoc.quickhass.slideover

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import fr.outadoc.quickhass.R


class SlideOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideover)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.slideover_content, SlideOverFragment.newInstance())
            .commit()

        window.setGravity(Gravity.BOTTOM)
    }
}
