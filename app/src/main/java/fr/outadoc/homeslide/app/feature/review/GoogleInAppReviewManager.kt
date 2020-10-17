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
