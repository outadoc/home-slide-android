package fr.outadoc.homeslide.app.onboarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentSetupShortcutBinding
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.app.onboarding.ui.ShortcutSetupFragmentDirections.Companion.actionSetupShortcutFragmentToSuccessFragment
import fr.outadoc.homeslide.app.onboarding.vm.ShortcutSetupViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ShortcutSetupFragment : Fragment() {

    private val vm: ShortcutSetupViewModel by viewModel()
    private var binding: FragmentSetupShortcutBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSetupShortcutBinding.inflate(inflater, container, false).apply {
            btnContinue.setOnClickListener {
                vm.onContinueClicked()
            }
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (it.pop()) {
                NavigationFlow.Next -> binding?.navController?.navigate(
                    actionSetupShortcutFragmentToSuccessFragment()
                )
                NavigationFlow.Back -> binding?.navController?.navigateUp()
                else -> Unit
            }
        }

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private val FragmentSetupShortcutBinding.navController: NavController
        get() = root.findNavController()

    companion object {
        fun newInstance() = ShortcutSetupFragment()
    }
}
