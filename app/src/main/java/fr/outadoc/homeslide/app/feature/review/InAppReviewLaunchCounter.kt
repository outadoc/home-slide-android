package fr.outadoc.homeslide.app.feature.review

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class InAppReviewLaunchCounter(context: Context) {

    companion object {
        private const val TRIGGER_THRESHOLD = 30
        private const val KEY_LAUNCH_COUNT = "pref_review_launch_count"
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val lock = Any()

    /**
     * Increments the counter.
     * @return true if the threshold was reached.
     */
    fun increment(): Boolean = synchronized(lock) {
        val count = sharedPreferences.getInt(KEY_LAUNCH_COUNT, 0)
        val newCount = (count + 1) % TRIGGER_THRESHOLD
        sharedPreferences.edit {
            putInt(KEY_LAUNCH_COUNT, newCount)
        }
        count == TRIGGER_THRESHOLD - 1
    }
}
