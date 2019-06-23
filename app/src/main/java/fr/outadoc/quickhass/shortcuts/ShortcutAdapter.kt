package fr.outadoc.quickhass.shortcuts

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.model.Shortcut
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue

class ShortcutAdapter : RecyclerView.Adapter<ShortcutAdapter.ViewHolder>() {

    val items: MutableList<Shortcut> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder) {
            val iconValue = try {
                if (item.icon != null) {
                    IconValue.valueOf(item.icon.toUpperCase())
                } else {
                    DEFAULT_ICON
                }
            } catch (e: IllegalArgumentException) {
                IconValue.ANDROID
            }

            val mdi = MaterialDrawableBuilder.with(icon.context)
                .setIcon(iconValue)
                .setColor(Color.parseColor("#fdd835"))
                .build()

            label.text = item.label
            icon.setImageDrawable(mdi)
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