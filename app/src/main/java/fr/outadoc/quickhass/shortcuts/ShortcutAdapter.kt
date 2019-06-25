package fr.outadoc.quickhass.shortcuts

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
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue

class ShortcutAdapter : RecyclerView.Adapter<ShortcutAdapter.ViewHolder>() {

    val items: MutableList<Entity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder) {
            val iconValue = item.getIconOrDefault()
            val mdi = MaterialDrawableBuilder.with(icon.context)
                .setIcon(iconValue)
                .setColor(Color.BLACK)
                .build()

            label.text = item.attributes.friendlyName ?: item.entityId
            icon.setImageDrawable(mdi)
        }
    }

    private fun Entity?.getIconOrDefault(): IconValue {
        if (this == null) return DEFAULT_ICON

        return attributes.icon?.toIcon() ?: when (domain) {
            "light" -> IconValue.LIGHTBULB
            "cover" -> IconValue.WINDOW_OPEN
            "person" -> IconValue.ACCOUNT
            "sun" -> IconValue.WEATHER_SUNNY
            "switch" -> IconValue.POWER_PLUG
            else -> DEFAULT_ICON
        }
    }

    private fun String.toIcon(): IconValue? {
        return try {
            IconValue.valueOf(this.takeLastWhile { it != ':' }.toUpperCase())
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tv_shortcut_label)
        val icon: ImageView = view.findViewById(R.id.imageView_shortcut_icon)
    }

    companion object {
        private val DEFAULT_ICON = IconValue.ANDROID
    }
}