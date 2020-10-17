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

package fr.outadoc.homeslide.app.feature.slideover.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.edit
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.databinding.FragmentSlideoverBinding
import fr.outadoc.homeslide.app.feature.grid.ui.EntityGridFragment
import fr.outadoc.homeslide.common.feature.review.InAppReviewManager
import org.koin.android.ext.android.inject

class SlideOverFragment : Fragment(), SlideOverNavigator {

    private var binding: FragmentSlideoverBinding? = null
    private val inAppReviewManager: InAppReviewManager by inject()
    private lateinit var sharedPreferences: SharedPreferences

    private var _additionalBottomInsets: Int = 0
    private var _peekHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        _peekHeight = sharedPreferences.getInt(
            KEY_LAST_PEEK_HEIGHT,
            resources.getDimension(R.dimen.slideover_peekHeight).toInt()
        )

        childFragmentManager
            .beginTransaction()
            .replace(R.id.content, EntityGridFragment.newInstance())
            .commit()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStack()
            } else {
                binding?.bottomSheetBehavior?.state = STATE_HIDDEN
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSlideoverBinding.inflate(inflater, container, false).apply {
            linearLayoutContent.setOnClickListener {
                // Prevent from bubbling event up to parent
            }

            root.setOnClickListener {
                activity?.finish()
            }

            setBottomSheetCallback()
            setWindowInsets()

            (activity as? AppCompatActivity)?.setSupportActionBar(gridToolbar)
        }

        return binding?.root
    }

    override fun navigateTo(fragment: Fragment) {
        setIsExpandable(true)

        childFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_out_down, R.anim.slide_in_down,
                R.anim.slide_in_up, R.anim.slide_out_up
            )
            .replace(R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updatePeekHeightAndInsets(
        peekHeight: Int? = null,
        bottomInsets: Int? = null,
        animate: Boolean = true
    ) {
        if (peekHeight != null) _peekHeight = peekHeight
        if (bottomInsets != null) _additionalBottomInsets = bottomInsets
        binding?.bottomSheetBehavior?.setPeekHeight(_peekHeight + _additionalBottomInsets, animate)
    }

    override fun updatePeekHeight(peekHeight: Int) {
        updatePeekHeightAndInsets(peekHeight = peekHeight)
        sharedPreferences.edit {
            putInt(KEY_LAST_PEEK_HEIGHT, peekHeight)
        }
    }

    override fun setIsExpandable(isExpandable: Boolean) {
        binding?.bottomSheetBehavior?.apply {
            isDraggable = isExpandable
            if (!isExpandable) state = STATE_COLLAPSED
        }
    }

    private fun FragmentSlideoverBinding.setBottomSheetCallback() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == STATE_HIDDEN) {
                    activity?.finish()
                }
            }
        })
    }

    private fun FragmentSlideoverBinding.setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            // Set top padding to account for status bar
            v.setPadding(0, insets.systemWindowInsetTop, 0, 0)

            updatePeekHeightAndInsets(
                bottomInsets = insets.systemWindowInsetBottom,
                animate = false
            )

            // Tell system we've consumed our insets
            WindowInsetsCompat.Builder()
                .setSystemWindowInsets(
                    Insets.of(
                        insets.systemWindowInsetLeft,
                        0,
                        insets.systemWindowInsetRight,
                        insets.systemWindowInsetBottom
                    )
                )
                .build()
        }
    }

    override fun onStop() {
        super.onStop()
        activity?.let { inAppReviewManager.askForStoreReview(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private val FragmentSlideoverBinding.bottomSheetBehavior: BottomSheetBehavior<*>
        get() {
            val p = linearLayoutContent.layoutParams as CoordinatorLayout.LayoutParams
            return p.behavior as BottomSheetBehavior
        }

    companion object {
        private const val KEY_LAST_PEEK_HEIGHT = "pref_last_peek_height"

        fun newInstance() = SlideOverFragment()
    }
}
