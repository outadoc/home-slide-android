package fr.outadoc.quickhass.feature.slideover.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import fr.outadoc.quickhass.R

class SlideOverFragment : Fragment() {

    private var viewHolder: ViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager
            .beginTransaction()
            .replace(R.id.content, EntityGridFragment.newInstance())
            .commit()
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

        return root
    }


    private fun setBottomSheetCallback(viewHolder: ViewHolder) {
        val p = viewHolder.contentContainer.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = p.behavior as BottomSheetBehavior

        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    activity?.finish()
                }
            }
        })
    }

    private fun setWindowInsets(viewHolder: ViewHolder) {
        with(viewHolder) {
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                v.setPadding(0, insets.systemWindowInsetTop, 0, 0)

                insets.replaceSystemWindowInsets(
                    insets.systemWindowInsetLeft,
                    0,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom
                )
            }
        }
    }

    private class ViewHolder(val root: View) {
        val contentContainer: FrameLayout = root.findViewById(R.id.frameLayout_content)
    }

    companion object {
        fun newInstance() = SlideOverFragment()
    }
}