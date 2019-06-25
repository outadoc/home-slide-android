package fr.outadoc.quickhass.quickaccess

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.model.Entity
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

class ShortcutAdapter(val onItemClick: (Entity) -> Unit) : RecyclerView.Adapter<ShortcutAdapter.ViewHolder>() {

    val items: MutableList<Entity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder) {
            val mdi = MaterialDrawableBuilder.with(icon.context)
                .setIcon(item.icon)
                .setColor(Color.BLACK)
                .build()

            view.isEnabled = item.isEnabled
            view.isActivated = item.isOn

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