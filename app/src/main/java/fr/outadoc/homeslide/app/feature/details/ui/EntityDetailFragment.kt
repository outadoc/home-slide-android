package fr.outadoc.homeslide.app.feature.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.feature.slideover.ui.EntityTileAdapter
import fr.outadoc.homeslide.common.feature.details.vm.EntityDetailViewModel
import fr.outadoc.homeslide.hassapi.factory.EntityFactory
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.Light
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity
import org.koin.androidx.viewmodel.ext.android.viewModel

class EntityDetailFragment private constructor() : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: EntityDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelable<EntityState>(ARG_STATE)?.let { state ->
            vm.setEntity(EntityFactory.create(state))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_entity_detail_container, container, false)

        val entityAdapter = EntityTileAdapter(
            onItemClickListener = {
                Toast.makeText(context, "lol click", Toast.LENGTH_SHORT).show()
            },
            onReorderedListener = { },
            onItemLongPressListener = { false },
            onItemVisibilityChangeListener = { _, _ -> },
            onItemHeightChangeListener = {}
        )

        viewHolder = ViewHolder(root, entityAdapter)

        /*vm.entity.observe(viewLifecycleOwner) { entity ->
            viewHolder?.itemAdapter?.apply {
                submitList(listOf(tileFactory.create(entity)))
            }
        }*/

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewHolder = null
    }

    private class ViewHolder(view: View, val itemAdapter: EntityTileAdapter) {
        val itemPreview = (view.findViewById(R.id.recyclerView_itemPreview) as RecyclerView).apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    companion object {

        fun newInstance(entity: Entity): EntityDetailFragment =
            EntityDetailFragment().apply {
                arguments = bundleOf(ARG_STATE to entity.state)
            }

        private const val ARG_STATE = "ARG_STATE"
    }
}
