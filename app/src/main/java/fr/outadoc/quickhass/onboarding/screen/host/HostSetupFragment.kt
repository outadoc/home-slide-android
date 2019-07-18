package fr.outadoc.quickhass.onboarding.screen.host

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import fr.outadoc.quickhass.R

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
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setup_host, container, false)

        viewHolder = ViewHolder(view).apply {
            baseUrlEditText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let { viewModel.onInstanceUrlChange(s.toString()) }
                }

            })
        }

        return view
    }

    private class ViewHolder(view: View) {
        val baseUrlEditText: EditText = view.findViewById(R.id.et_instance_base_url)
        val discoveryResult: TextView = view.findViewById(R.id.lbl_discovery_result)
    }

    companion object {
        fun newInstance() = HostSetupFragment()
    }
}
