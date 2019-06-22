package fr.outadoc.quickhass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.model.Shortcut


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

        viewModel.shortcuts.observe(this, Observer<List<Shortcut>> { shortcuts ->
            viewHolder?.itemAdapter?.apply {
                items.clear()
                items.addAll(shortcuts)
                notifyDataSetChanged()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_quick_access, container, false)

        viewHolder = ViewHolder(view, ShortcutAdapter()).apply {
            contentContainer.setOnClickListener {
                // Prevent from bubbling event up to parent
            }

            view.setOnClickListener {
                activity?.finish()
            }

            ViewCompat.setOnApplyWindowInsetsListener(contentContainer) { contentContainer, insets ->
                contentContainer.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
                view.setPadding(0, insets.systemWindowInsetTop, 0, 0)
                insets.consumeSystemWindowInsets()
            }

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)

            with(recyclerView) {
                adapter = itemAdapter
                layoutManager = GridLayoutManager(context, GRID_SPAN_COUNT)
                addItemDecoration(GridSpacingItemDecoration(GRID_SPAN_COUNT, spacingInPixels))
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private class ViewHolder(view: View, val itemAdapter: ShortcutAdapter) {
        val contentContainer: ViewGroup = view.findViewById(R.id.container_quick_access)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_shortcuts)
    }
}