package fr.outadoc.homeslide.app.feature.slideover.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import fr.outadoc.homeslide.app.feature.grid.ui.EntityGridFragment

class SlideOverFragment : Fragment(), SlideOverNavigator {

    private var viewHolder: ViewHolder? = null

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
                viewHolder?.bottomSheetBehavior?.state = STATE_HIDDEN
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_slideover, container, false)

        viewHolder = ViewHolder(root).apply {
            contentContainer.setOnClickListener {
                // Prevent from bubbling event up to parent
            }

            root.setOnClickListener {
                activity?.finish()
            }

            setBottomSheetCallback(this)
            setWindowInsets(this)
        }

        val toolbar: Toolbar = root.findViewById(R.id.grid_toolbar)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        return root
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
        viewHolder?.bottomSheetBehavior?.state = STATE_COLLAPSED
    }

    override fun restoreSheet() {
        viewHolder?.bottomSheetBehavior?.state = STATE_EXPANDED
    }

    private fun setBottomSheetCallback(viewHolder: ViewHolder) {
        viewHolder.bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == STATE_HIDDEN) {
                    activity?.finish()
                }
            }
        })
    }

    private fun setWindowInsets(viewHolder: ViewHolder) {
        with(viewHolder) {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewHolder = null
    }

    private class ViewHolder(val root: View) {
        val contentContainer: LinearLayout = root.findViewById(R.id.linearLayout_content)
        val bottomSheetBehavior: BottomSheetBehavior<*>
            get() {
                val p = contentContainer.layoutParams as CoordinatorLayout.LayoutParams
                return p.behavior as BottomSheetBehavior
            }
    }

    companion object {
        fun newInstance() = SlideOverFragment()
    }
}