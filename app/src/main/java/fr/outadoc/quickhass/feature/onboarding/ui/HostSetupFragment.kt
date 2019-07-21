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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.feature.onboarding.vm.HostSetupViewModel

class HostSetupFragment : Fragment() {

    private lateinit var viewHolder: ViewHolder
    private lateinit var viewModel: HostSetupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HostSetupViewModel::class.java).apply {
            instanceDiscoveryInfo.observe(this@HostSetupFragment, Observer { discovery ->
                viewHolder.discoveryResult.text =
                        when (if (discovery.isSuccess) discovery.getOrNull() else null) {
                            null -> "❌"
                            else -> "✅"
                        }
            })

            canContinue.observe(this@HostSetupFragment, Observer { canContinue ->
                viewHolder.continueButton.isEnabled = canContinue
            })

            navigateTo.observe(this@HostSetupFragment, Observer {
                when (it.pop()) {
                    NavigationFlow.NEXT -> viewHolder.navController.navigate(R.id.action_setupHostFragment_to_setupAuthFragment)
                    else -> viewHolder.navController.navigateUp()
                }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setup_host, container, false)

        viewHolder = ViewHolder(view).apply {
            baseUrlEditText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let { viewModel.onInstanceUrlChanged(s.toString()) }
                }
            })

            continueButton.setOnClickListener {
                viewModel.onContinueClicked()
            }
        }

        return view
    }

    private class ViewHolder(private val view: View) {
        val baseUrlEditText: EditText = view.findViewById(R.id.et_instance_base_url)
        val discoveryResult: TextView = view.findViewById(R.id.lbl_discovery_result)
        val continueButton: Button = view.findViewById(R.id.btn_continue)

        val navController: NavController
            get() = view.findNavController()
    }

    companion object {
        fun newInstance() = HostSetupFragment()
    }
}
