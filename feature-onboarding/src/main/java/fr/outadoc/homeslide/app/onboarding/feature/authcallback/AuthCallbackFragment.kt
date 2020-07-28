package fr.outadoc.homeslide.app.onboarding.feature.authcallback

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentAuthCallbackBinding
import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import io.uniflow.androidx.flow.onEvents
import org.koin.android.viewmodel.ext.android.viewModel

class AuthCallbackFragment : Fragment() {

    private val vm: AuthCallbackViewModel by viewModel()
    private var binding: FragmentAuthCallbackBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthCallbackBinding.inflate(inflater, container, false).apply {
            (iconLoading.drawable as? AnimatedVectorDrawable)?.start()
        }

        onEvents(vm) { event ->
            when (event.take()) {
                NavigationEvent.Next -> {
                    navigate(AuthCallbackFragmentDirections.setupShortcutsAction())
                }
            }
        }

        requireArguments().getString(ARG_CODE)?.let { code ->
            // We're coming from a deeplink and we got an authentication code
            vm.onAuthCallback(code)
        }

        return binding?.root
    }

    private fun navigate(directions: NavDirections) {
        binding?.navController?.navigate(directions)
    }

    private val FragmentAuthCallbackBinding.navController: NavController
        get() = root.findNavController()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val ARG_CODE = "code"
    }
}
