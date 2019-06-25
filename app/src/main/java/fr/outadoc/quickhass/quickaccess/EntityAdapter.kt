package fr.outadoc.quickhass.quickaccess

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.model.Entity
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

class EntityAdapter(val onItemClick: (Entity) -> Unit) : RecyclerView.Adapter<EntityAdapter.ViewHolder>() {

    val items: MutableList<Entity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder) {
            // Disable the view if it can't be controlled
            view.isEnabled = item.isEnabled

            // Activate the view if the entity is "on"
            view.isActivated = item.isOn

            // We apparently can't directly tint the ImageView with this lib...
            // so we retrieve the ColorStateList and get the right color for the view's state
            val csl = ContextCompat.getColorStateList(icon.context, R.color.color_on_background_shortcut_item)
            val mdi = MaterialDrawableBuilder.with(icon.context)
                .setIcon(item.icon)
                .setColor(csl?.getColorForState(icon.drawableState, Color.BLACK) ?: Color.BLACK)
                .build()

            view.setOnClickListener { onItemClick(item) }

            label.text = item.friendlyName ?: item.entityId
            icon.setImageDrawable(mdi)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tv_shortcut_label)
        val icon: ImageView = view.findViewById(R.id.imageView_shortcut_icon)
    }

}