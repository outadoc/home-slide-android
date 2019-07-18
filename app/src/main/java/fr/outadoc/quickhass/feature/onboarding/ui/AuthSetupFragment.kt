package fr.outadoc.quickhass.feature.onboarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.outadoc.quickhass.R

class AuthSetupFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setup_auth, container, false)
    }

    companion object {
        fun newInstance() =
            AuthSetupFragment()
    }
}
