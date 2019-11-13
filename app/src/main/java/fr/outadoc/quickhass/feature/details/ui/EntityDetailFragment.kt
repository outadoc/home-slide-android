package fr.outadoc.quickhass.feature.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.details.vm.EntityDetailViewModel
import fr.outadoc.quickhass.feature.slideover.model.EntityState
import fr.outadoc.quickhass.feature.slideover.model.entity.Entity
import fr.outadoc.quickhass.feature.slideover.model.entity.EntityFactory
import fr.outadoc.quickhass.feature.slideover.model.entity.LightEntity
import fr.outadoc.quickhass.feature.slideover.ui.EntityAdapter
import org.koin.android.viewmodel.ext.android.viewModel

class EntityDetailFragment private constructor() : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: EntityDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelable<EntityState>(ARGS_STATE)?.let { state ->
            vm.setEntity(EntityFactory.create(state))

            childFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout_detailsContent, getChildFragment(state))
                .commit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_entity_detail_container, container, false)

        val entityAdapter = EntityAdapter({
            Toast.makeText(context, "lol click", Toast.LENGTH_SHORT).show()
        }, onReorderedListener = { }, onItemLongPress = { false })

        viewHolder = ViewHolder(root, entityAdapter)

        vm.entity.observe(viewLifecycleOwner) { entity ->
            viewHolder?.itemAdapter?.apply {
                submitList(listOf(entity))
                notifyDataSetChanged()
            }
        }

        return root
    }

    private fun getChildFragment(state: EntityState): Fragment =
        when (state.domain) {
            LightEntity.DOMAIN -> LightEntityDetailFragment.newInstance(state)
            else -> throw IllegalArgumentException("No detail fragment for ${state.domain}")
        }

    override fun onDestroy() {
        super.onDestroy()
        viewHolder = null
    }

    private class ViewHolder(view: View, val itemAdapter: EntityAdapter) {
        val itemPreview = (view.findViewById(R.id.recyclerView_itemPreview) as RecyclerView).apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    companion object {

        private fun hasDetailsScreen(entity: Entity) =
            entity.domain in listOf(LightEntity.DOMAIN)

        fun newInstance(entity: Entity) =
            if (hasDetailsScreen(entity)) {
                EntityDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARGS_STATE, entity.state)
                    }
                }
            } else null

        private const val ARGS_STATE = "ARGS_STATE"
    }
}