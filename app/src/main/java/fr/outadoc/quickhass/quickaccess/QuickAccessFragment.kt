package fr.outadoc.quickhass.quickaccess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import fr.outadoc.quickhass.GridSpacingItemDecoration
import fr.outadoc.quickhass.MainActivity
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.model.Entity


class QuickAccessFragment : Fragment() {

    companion object {
        fun newInstance() = QuickAccessFragment()
        const val GRID_SPAN_COUNT = 3
    }

    private lateinit var viewModel: QuickAccessViewModel

    private var viewHolder: ViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(QuickAccessViewModel::class.java)

        with(viewModel) {
            shortcuts.observe(this@QuickAccessFragment, Observer { shortcuts ->
                viewHolder?.itemAdapter?.apply {
                    items.clear()
                    items.addAll(shortcuts)
                    notifyDataSetChanged()
                }
            })

            error.observe(this@QuickAccessFragment, Observer { e ->
                Toast.makeText(context, e.message ?: "An error occurred.", Toast.LENGTH_SHORT)
                    .show()
            })

            isLoading.observe(this@QuickAccessFragment, Observer { isLoading ->
                viewHolder?.apply {
                    progress.isVisible = isLoading
                }
            })

            loadShortcuts()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_quick_access, container, false)

        viewHolder = ViewHolder(
            root,
            EntityAdapter { entity: Entity ->
                viewModel.onEntityClick(entity)
            }

        ).apply {
            contentContainer.setOnClickListener {
                // Prevent from bubbling event up to parent
            }

            root.setOnClickListener {
                activity?.finish()
            }

            settingsButton.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            with(recyclerView) {
                adapter = itemAdapter
                layoutManager = GridLayoutManager(
                    context,
                    GRID_SPAN_COUNT
                )

                addItemDecoration(
                    GridSpacingItemDecoration(
                        GRID_SPAN_COUNT,
                        resources.getDimensionPixelSize(R.dimen.grid_spacing)
                    )
                )
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

            ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { v, insets ->
                val newPaddingBottom = insets.systemWindowInsetBottom + constraintLayoutContainer.paddingTop
                v.setPadding(0, 0, 0, newPaddingBottom)

                insets.replaceSystemWindowInsets(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    0
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }

    private class ViewHolder(val root: View, val itemAdapter: EntityAdapter) {
        val contentContainer: FrameLayout = root.findViewById(R.id.frameLayout_content)
        val constraintLayoutContainer: ConstraintLayout = root.findViewById(R.id.constraintLayout_content)
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView_shortcuts)
        val settingsButton: ImageButton = root.findViewById(R.id.imageButton_settings)
        val progress: ProgressBar = root.findViewById(R.id.progress_main)
    }
}