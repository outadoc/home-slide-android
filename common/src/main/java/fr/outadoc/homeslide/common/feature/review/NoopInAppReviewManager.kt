package fr.outadoc.homeslide.common.feature.review

import android.app.Activity

class NoopInAppReviewManager : InAppReviewManager {
    override fun askForStoreReview(activity: Activity) = Unit
}