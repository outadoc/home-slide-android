package fr.outadoc.homeslide.app.onboarding.feature.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentWelcomeBinding
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import org.koin.android.viewmodel.ext.android.viewModel

class WelcomeFragment : Fragment() {

    private val vm: WelcomeViewModel by viewModel()
    private var binding: FragmentWelcomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false).apply {
            btnContinue.setOnClickListener {
                vm.onContinueClicked()
            }
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (it.pop()) {
                NavigationFlow.Next -> binding?.navController?.navigate(
                    WelcomeFragmentDirections.setupHostAction()
                )
                NavigationFlow.Back -> binding?.navController?.navigateUp()
                else -> Unit
            }
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private val FragmentWelcomeBinding.navController: NavController
        get() = root.findNavController()
}
