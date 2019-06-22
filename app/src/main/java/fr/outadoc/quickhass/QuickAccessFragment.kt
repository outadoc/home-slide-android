package fr.outadoc.quickhass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment

class QuickAccessFragment private constructor() : Fragment() {

    companion object {
        fun newInstance() = QuickAccessFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_quick_access, container, false)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }
}