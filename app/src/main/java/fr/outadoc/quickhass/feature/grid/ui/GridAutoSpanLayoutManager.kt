package fr.outadoc.quickhass.feature.grid.ui

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max


class GridAutoSpanLayoutManager(context: Context, private val columnWidth: Int) : GridLayoutManager(context, 1) {

    private var lastWidth: Int = 0
    private var lastHeight: Int = 0

    private val hasValidSize: Boolean
        get() = columnWidth > 0 && width > 0 && height > 0

    private val hasSizeChanged: Boolean
        get() = lastWidth != width || lastHeight != height

    private val totalSize: Int
        get() = when (orientation) {
            LinearLayoutManager.VERTICAL -> width - paddingRight - paddingLeft
            else -> height - paddingTop - paddingBottom
        }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (hasValidSize && hasSizeChanged) {
            spanCount = max(1, totalSize / columnWidth)
        }

        lastWidth = width
        lastHeight = height

        super.onLayoutChildren(recycler, state)
    }
}