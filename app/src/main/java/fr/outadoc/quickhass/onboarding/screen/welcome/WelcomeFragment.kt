package fr.outadoc.quickhass.onboarding.screen.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import fr.outadoc.quickhass.R

class WelcomeFragment : Fragment() {

    private lateinit var viewHolder: ViewHolder

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        viewHolder = ViewHolder(view).apply {
            continueButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_welcomeFragment_to_setupHostFragment)
            }
        }

        return view
    }

    private class ViewHolder(view: View) {
        val continueButton: Button = view.findViewById(R.id.btn_continue)
    }

    companion object {
        fun newInstance() = WelcomeFragment()
    }
}
