package fr.outadoc.quickhass.feature.slideover.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.slideover.model.EntityState
import fr.outadoc.quickhass.feature.slideover.model.entity.EntityFactory
import fr.outadoc.quickhass.feature.slideover.model.entity.LightEntity

class LightEntityDetailFragment private constructor() : Fragment() {

    private var viewHolder: ViewHolder? = null
    private var entity: LightEntity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_entity_detail_light, container, false)

        viewHolder = ViewHolder(root).apply {
            val brightness = entity?.brightness
            seekBar.progress = when {
                brightness != null -> brightness
                entity?.isOn == true -> 100
                else -> 0
            }
        }

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelable<EntityState>(ARGS_STATE)?.let { state ->
            entity = EntityFactory.create(state) as? LightEntity
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewHolder = null
    }

    private class ViewHolder(root: View) {
        val seekBar: SeekBar = root.findViewById(R.id.seekBar_light_level)
    }

    companion object {

        fun newInstance(state: EntityState): LightEntityDetailFragment =
            LightEntityDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGS_STATE, state)
                }
            }

        private const val ARGS_STATE = "ARGS_STATE"
    }
}