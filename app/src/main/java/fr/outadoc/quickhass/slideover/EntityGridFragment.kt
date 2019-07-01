package fr.outadoc.quickhass.slideover

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.GridSpacingItemDecoration
import fr.outadoc.quickhass.MainActivity
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.model.Entity


class EntityGridFragment private constructor() : Fragment() {

    companion object {
        fun newInstance() = EntityGridFragment()
        const val GRID_SPAN_COUNT = 3
    }

    private lateinit var viewModel: EntityGridViewModel

    private var viewHolder: ViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(EntityGridViewModel::class.java)

        with(viewModel) {
            shortcuts.observe(this@EntityGridFragment, Observer { shortcuts ->
                viewHolder?.itemAdapter?.apply {
                    items.clear()
                    items.addAll(shortcuts)
                    notifyDataSetChanged()
                }
            })

            error.observe(this@EntityGridFragment, Observer { e ->
                Toast.makeText(context, e.message ?: "An error occurred.", Toast.LENGTH_SHORT)
                    .show()
            })

            isLoading.observe(this@EntityGridFragment, Observer { isLoading ->
                viewHolder?.apply {
                    progress.isVisible = isLoading
                }
            })

            shouldAskForInitialValues.observe(
                this@EntityGridFragment,
                Observer { shouldAskForInitialValues ->
                    if (shouldAskForInitialValues) {
                        Toast.makeText(
                            context,
                            "You must configure the app before using it.",
                            Toast.LENGTH_LONG
                        )
                            .show()

                        val i = Intent(activity, MainActivity::class.java)
                        startActivity(i)
                    }
                })

            loadShortcuts()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_entity_grid, container, false)

        viewHolder = ViewHolder(
            root,
            EntityAdapter { entity: Entity ->
                viewModel.onEntityClick(entity)
            }

        ).apply {
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

            setWindowInsets(this)
        }

        return root
    }

    private fun setWindowInsets(viewHolder: ViewHolder) {
        with(viewHolder) {
            ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { v, insets ->
                val newPaddingBottom = insets.systemWindowInsetBottom + root.paddingTop
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
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView_shortcuts)
        val settingsButton: ImageButton = root.findViewById(R.id.imageButton_settings)
        val progress: ProgressBar = root.findViewById(R.id.progress_main)
    }
}