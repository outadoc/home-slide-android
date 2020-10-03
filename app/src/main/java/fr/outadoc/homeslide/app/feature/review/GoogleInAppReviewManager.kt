package fr.outadoc.homeslide.app.feature.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManager
import fr.outadoc.homeslide.common.feature.review.InAppReviewManager

class GoogleInAppReviewManager(
    private val manager: ReviewManager,
    private val counter: InAppReviewLaunchCounter
) : InAppReviewManager {

    override fun askForStoreReview(activity: Activity) {
        if (counter.increment()) {
            manager.requestReviewFlow()
                .addOnCompleteListener { request ->
                    if (request.isSuccessful) {
                        val reviewInfo = request.result
                        manager.launchReviewFlow(activity, reviewInfo)
                    }
                }
        }
    }
}
