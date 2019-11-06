package fr.outadoc.quickhass.feature.onboarding.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.onboarding.extensions.toViewStatus
import fr.outadoc.quickhass.feature.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.feature.onboarding.vm.AuthSetupViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AuthSetupFragment : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: AuthSetupViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setup_auth, container, false)

        viewHolder = ViewHolder(view).apply {
            tokenEditText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let { vm.onTokenChanged(s.toString()) }
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
                NavigationFlow.Next -> viewHolder?.navController?.navigate(R.id.action_setupAuthFragment_to_setupShortcutFragment)
                NavigationFlow.Back -> viewHolder?.navController?.navigateUp()
                is NavigationFlow.Url -> {
                    val intent = CustomTabsIntent.Builder().build()
                    intent.launchUrl(context, dest.url)
                }
            }
        }

        return view
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
        fun newInstance() = AuthSetupFragment()
    }
}
