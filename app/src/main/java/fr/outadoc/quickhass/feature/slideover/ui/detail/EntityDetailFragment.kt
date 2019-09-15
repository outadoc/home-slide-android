package fr.outadoc.quickhass.feature.slideover.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.slideover.model.EntityState
import fr.outadoc.quickhass.feature.slideover.model.entity.Entity
import fr.outadoc.quickhass.feature.slideover.model.entity.EntityFactory
import fr.outadoc.quickhass.feature.slideover.model.entity.LightEntity
import kotlin.properties.Delegates

class EntityDetailFragment private constructor() : Fragment() {

    private var viewHolder: ViewHolder? = null
    private var entity: Entity? by Delegates.observable(null) { _, _: Entity?, newValue: Entity? ->
        newValue?.let {
            viewHolder?.entityName?.text = it.friendlyName
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_entity_detail_container, container, false)
        viewHolder = ViewHolder(root).apply {
            backButton.setOnClickListener {
                activity?.onBackPressed()
            }

            entityName.text = entity?.friendlyName
        }
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelable<EntityState>(ARGS_STATE)?.let { state ->
            entity = EntityFactory.create(state) as? LightEntity

            childFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout_detailsContent, getChildFragment(state))
                .commit()
        }
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

    private class ViewHolder(view: View) {
        val backButton: ImageButton = view.findViewById(R.id.imageButton_back)
        val entityName: TextView = view.findViewById(R.id.textView_entityName)
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