/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
