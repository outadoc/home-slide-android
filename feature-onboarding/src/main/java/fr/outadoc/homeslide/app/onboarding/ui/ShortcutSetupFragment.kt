package fr.outadoc.homeslide.app.onboarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import fr.outadoc.homeslide.app.onboarding.R
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.app.onboarding.ui.ShortcutSetupFragmentDirections.Companion.actionSetupShortcutFragmentToSuccessFragment
import fr.outadoc.homeslide.app.onboarding.vm.ShortcutSetupViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ShortcutSetupFragment : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: ShortcutSetupViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setup_shortcut, container, false)

        viewHolder = ViewHolder(view).apply {
            continueButton.setOnClickListener {
                vm.onContinueClicked()
            }
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (it.pop()) {
                NavigationFlow.Next -> viewHolder?.navController?.navigate(
                    actionSetupShortcutFragmentToSuccessFragment()
                )
                NavigationFlow.Back -> viewHolder?.navController?.navigateUp()
                else -> Unit
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewHolder = null
    }

    private class ViewHolder(private val view: View) {
        val continueButton: Button = view.findViewById(R.id.btn_continue)

        val navController: NavController
            get() = view.findNavController()
    }

    companion object {
        fun newInstance() = ShortcutSetupFragment()
    }
}
