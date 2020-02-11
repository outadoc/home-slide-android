package fr.outadoc.homeslide.app.onboarding.ui

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import fr.outadoc.homeslide.app.onboarding.R
import fr.outadoc.homeslide.app.onboarding.extensions.toViewStatus
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.app.onboarding.ui.LegacyAuthSetupFragmentDirections.Companion.actionSetupLegacyAuthFragmentToAuthenticationCustomTabs
import fr.outadoc.homeslide.app.onboarding.ui.LegacyAuthSetupFragmentDirections.Companion.actionSetupLegacyAuthFragmentToSetupShortcutFragment
import fr.outadoc.homeslide.app.onboarding.ui.LegacyAuthSetupFragmentDirections.Companion.actionSetupLegacyAuthFragmentToSuccessFragment
import fr.outadoc.homeslide.app.onboarding.vm.LegacyAuthSetupViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LegacyAuthSetupFragment : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: LegacyAuthSetupViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setup_legacyauth, container, false)

        viewHolder = ViewHolder(view).apply {
            tokenEditText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let { vm.onTokenChanged(s.toString().trim()) }
                }
            })

            continueButton.setOnClickListener {
                vm.onContinueClicked()
            }

            helpLink.setOnClickListener {
                vm.onClickHelpLink()
            }
        }

        vm.apiStatus.observe(viewLifecycleOwner) { status ->
            viewHolder?.tokenValidationResult?.state = status.toViewStatus()
        }

        vm.canContinue.observe(viewLifecycleOwner) { canContinue ->
            viewHolder?.continueButton?.isEnabled = canContinue
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (val dest = it.pop()) {
                NavigationFlow.Next -> {
                    val nextFragment = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        // If the OS doesn't support quick settings, skip the quick settings step
                        actionSetupLegacyAuthFragmentToSuccessFragment()
                    } else {
                        actionSetupLegacyAuthFragmentToSetupShortcutFragment()
                    }

                    viewHolder?.navController?.navigate(nextFragment)
                }
                NavigationFlow.Back -> viewHolder?.navController?.navigateUp()

                is NavigationFlow.Url -> {
                    navigate(
                        actionSetupLegacyAuthFragmentToAuthenticationCustomTabs(
                            dest.url
                        )
                    )
                }
            }
        }

        return view
    }

    private fun navigate(directions: NavDirections) {
        viewHolder?.navController?.navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewHolder = null
    }

    private class ViewHolder(private val view: View) {
        val tokenEditText: EditText = view.findViewById(R.id.et_token)
        val tokenValidationResult: ResultIconView = view.findViewById(R.id.view_token_result)
        val continueButton: Button = view.findViewById(R.id.btn_continue)
        val helpLink: Button = view.findViewById(R.id.btn_auth_help_link)

        val navController: NavController
            get() = view.findNavController()
    }

    companion object {
        fun newInstance() = LegacyAuthSetupFragment()
    }
}
