package fr.outadoc.homeslide.app.feature.slideover.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.databinding.FragmentSlideoverBinding
import fr.outadoc.homeslide.app.feature.grid.ui.EntityGridFragment

class SlideOverFragment : Fragment(), SlideOverNavigator {

    private var binding: FragmentSlideoverBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    ): View {
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

        return binding!!.root
    }

    override fun navigateTo(fragment: Fragment) {
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

    override fun collapseSheet() {
        binding?.bottomSheetBehavior?.state = STATE_COLLAPSED
    }

    override fun restoreSheet() {
        binding?.bottomSheetBehavior?.state = STATE_EXPANDED
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

            val peekHeight = resources.getDimension(R.dimen.slideover_peekHeight).toInt()
            bottomSheetBehavior.peekHeight = peekHeight + insets.systemWindowInsetBottom

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
        fun newInstance() = SlideOverFragment()
    }
}