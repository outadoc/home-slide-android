package fr.outadoc.quickhass.feature.slideover.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.slideover.model.EntityState
import fr.outadoc.quickhass.feature.slideover.model.entity.Entity
import fr.outadoc.quickhass.feature.slideover.model.entity.LightEntity

class EntityDetailFragment private constructor() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_entity_detail_container, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelable<EntityState>(ARGS_STATE)?.let { state ->
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