package fr.outadoc.homeslide.app.onboarding.ui

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isGone
import fr.outadoc.homeslide.app.onboarding.R
import kotlin.properties.Delegates

class ResultIconView : LinearLayout {

    private val errorView: View
    private val successView: View
    private val loadingView: View

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {

        View.inflate(context, R.layout.view_onboarding_resulticon, this).let {
            successView = it.findViewById(R.id.icon_success)
            errorView = it.findViewById(R.id.icon_error)
            loadingView = it.findViewById<ImageView>(R.id.icon_loading).apply {
                (drawable as? AnimatedVectorDrawable)?.start()
            }
        }

        hideAll()
    }

    private fun hideAll() {
        errorView.isGone = true
        successView.isGone = true
        loadingView.isGone = true
    }

    private fun getViewForState(state: State): View? {
        return when (state) {
            State.SUCCESS -> successView
            State.ERROR -> errorView
            State.LOADING -> loadingView
            else -> null
        }
    }

    var state: State by Delegates.observable(State.NONE) { _, oldValue: State, newValue: State ->
        if (oldValue != newValue) {
            hideAll()
            getViewForState(newValue)?.isGone = false
        }
    }

    enum class State {
        NONE, SUCCESS, ERROR, LOADING
    }
}