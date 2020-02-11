package fr.outadoc.homeslide.app.onboarding.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import fr.outadoc.homeslide.app.onboarding.R
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.app.onboarding.ui.AuthSetupFragmentDirections.Companion.actionSetupAuthFragmentToAuthenticationCustomTabs
import fr.outadoc.homeslide.app.onboarding.ui.AuthSetupFragmentDirections.Companion.actionSetupAuthFragmentToSetupShortcutFragment
import fr.outadoc.homeslide.app.onboarding.ui.AuthSetupFragmentDirections.Companion.actionSetupAuthFragmentToSuccessFragment
import fr.outadoc.homeslide.app.onboarding.vm.AuthSetupViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AuthSetupFragment : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: AuthSetupViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setup_auth, container, false)

        viewHolder = ViewHolder(view).apply {
            signInButton.setOnClickListener {
                vm.onSignInClick()
            }
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (val dest = it.pop()) {
                NavigationFlow.Next -> {
                    val direction = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        // If the OS doesn't support quick settings, skip the quick settings step
                        actionSetupAuthFragmentToSuccessFragment()
                    } else {
                        actionSetupAuthFragmentToSetupShortcutFragment()
                    }

                    navigate(direction)
                }

                NavigationFlow.Back -> viewHolder?.navController?.navigateUp()

                is NavigationFlow.Url -> {
                    navigate(actionSetupAuthFragmentToAuthenticationCustomTabs(dest.url))
                }
            }
        }

        arguments?.getString("code")?.let { code ->
            // We're coming from a deeplink and we got an authentication code
            vm.onAuthCallback(code)
        }

        return view
    }

    private fun navigate(directions: NavDirections) {
        viewHolder?.navController?.navigate(directions)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewHolder = null
    }

    private class ViewHolder(private val view: View) {
        val signInButton: Button = view.findViewById(R.id.btn_sign_in)

        val navController: NavController
            get() = view.findNavController()
    }

    companion object {
        fun newInstance() = AuthSetupFragment()
    }
}