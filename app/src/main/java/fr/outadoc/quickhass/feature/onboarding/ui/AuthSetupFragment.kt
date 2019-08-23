package fr.outadoc.quickhass.feature.onboarding.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.onboarding.extensions.toViewStatus
import fr.outadoc.quickhass.feature.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.feature.onboarding.vm.AuthSetupViewModel

class AuthSetupFragment : Fragment() {

    private lateinit var viewHolder: ViewHolder
    private val viewModel: AuthSetupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.apiStatus.observe(this, Observer { status ->
            viewHolder.tokenValidationResult.state = status.toViewStatus()
        })

        viewModel.canContinue.observe(this, Observer { canContinue ->
            viewHolder.continueButton.isEnabled = canContinue
        })

        viewModel.navigateTo.observe(this, Observer {
            when (val dest = it.pop()) {
                NavigationFlow.Next -> viewHolder.navController.navigate(R.id.action_setupAuthFragment_to_setupShortcutFragment)
                NavigationFlow.Back -> viewHolder.navController.navigateUp()
                is NavigationFlow.Url -> {
                    val intent = CustomTabsIntent.Builder().build()
                    intent.launchUrl(context, dest.url)
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setup_auth, container, false)

        viewHolder = ViewHolder(view).apply {
            tokenEditText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let { viewModel.onTokenChanged(s.toString()) }
                }
            })

            continueButton.setOnClickListener {
                viewModel.onContinueClicked()
            }

            helpLink.setOnClickListener {
                viewModel.onClickHelpLink()
            }
        }

        return view
    }

    private class ViewHolder(private val view: View) {
        val tokenEditText: EditText = view.findViewById(R.id.et_token)
        val tokenValidationResult: ResultIconView = view.findViewById(R.id.view_token_result)
        val continueButton: Button = view.findViewById(R.id.btn_continue)
        val helpLink: TextView = view.findViewById(R.id.lbl_auth_help_link)

        val navController: NavController
            get() = view.findNavController()
    }

    companion object {
        fun newInstance() = AuthSetupFragment()
    }
}
