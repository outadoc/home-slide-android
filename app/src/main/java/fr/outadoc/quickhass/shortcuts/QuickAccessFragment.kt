package fr.outadoc.quickhass.shortcuts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.GridSpacingItemDecoration
import fr.outadoc.quickhass.MainActivity
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.model.State


class QuickAccessFragment private constructor() : Fragment() {

    companion object {
        fun newInstance() = QuickAccessFragment()
        const val GRID_SPAN_COUNT = 3
    }

    private lateinit var viewModel: QuickAccessViewModel

    private var viewHolder: ViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(QuickAccessViewModel::class.java)

        viewModel.shortcuts.observe(this, Observer<List<State>> { shortcuts ->
            viewHolder?.itemAdapter?.apply {
                items.clear()
                items.addAll(shortcuts)
                notifyDataSetChanged()
            }
        })

        viewModel.loadShortcuts()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_quick_access, container, false)

        viewHolder = ViewHolder(
            root,
            ShortcutAdapter()
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

        return root
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

    private class ViewHolder(view: View, val itemAdapter: ShortcutAdapter) {
        val contentContainer: FrameLayout = view.findViewById(R.id.frameLayout_content)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_shortcuts)
        val settingsButton: ImageButton = view.findViewById(R.id.imageButton_settings)
    }
}