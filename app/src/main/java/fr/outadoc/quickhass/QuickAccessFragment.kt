package fr.outadoc.quickhass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment


class QuickAccessFragment private constructor() : Fragment() {

    companion object {
        fun newInstance() = QuickAccessFragment()
    }

    private var viewHolder: ViewHolder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_quick_access, container, false)

        viewHolder = ViewHolder(view).apply {
            contentContainer.setOnClickListener {
                // Prevent from bubbling event up to parent
            }

            view.setOnClickListener {
                activity?.finish()
            }

            ViewCompat.setOnApplyWindowInsetsListener(contentContainer) { view, insets ->
                view.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
                insets.consumeSystemWindowInsets()
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

    private class ViewHolder(view: View) {
        val contentContainer: ViewGroup = view.findViewById(R.id.container_quick_access)
    }
}