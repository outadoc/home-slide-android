package fr.outadoc.homeslide.wear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import fr.outadoc.homeslide.common.feature.consent.ConsentPreferenceRepository
import fr.outadoc.homeslide.wear.feature.list.EntityListFragment
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val consentPrefs: ConsentPreferenceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(consentPrefs.isCrashReportingEnabled)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_wearActivity_content, EntityListFragment.newInstance())
            .commit()
    }
}
