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
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.onboarding.extensions.toViewStatus
import fr.outadoc.quickhass.feature.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.feature.onboarding.vm.HostSetupViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class HostSetupFragment : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: HostSetupViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setup_host, container, false)

        viewHolder = ViewHolder(view, ZeroconfAdapter { vm.onZeroconfHostSelected(it) }).apply {
            baseUrlEditText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let { vm.onInstanceUrlChanged(s.toString()) }
                }
            })

            continueButton.setOnClickListener {
                vm.onContinueClicked()
            }
        }

        vm.instanceDiscoveryInfo.observe(viewLifecycleOwner) { discovery ->
            viewHolder?.discoveryResult?.state = discovery.toViewStatus()
        }

        vm.autoDiscoveredInstances.observe(viewLifecycleOwner) { autoDiscovered ->
            viewHolder?.zeroconfHelper?.isInvisible = autoDiscovered.isEmpty()

            viewHolder?.zeroconfAdapter?.apply {
                items.clear()
                items.addAll(autoDiscovered)
                notifyDataSetChanged()
                viewHolder?.zeroconfRecyclerView?.scheduleLayoutAnimation()
            }
        }

        vm.canContinue.observe(viewLifecycleOwner) { canContinue ->
            viewHolder?.continueButton?.isEnabled = canContinue
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (it.pop()) {
                NavigationFlow.Next -> viewHolder?.navController?.navigate(R.id.action_setupHostFragment_to_setupAuthFragment)
                NavigationFlow.Back -> viewHolder?.navController?.navigateUp()
                else -> Unit
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewHolder?.baseUrlEditText?.apply {
            if (text.isEmpty()) {
                setText(vm.defaultInstanceUrl)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        vm.stopDiscovery()
    }

    override fun onResume() {
        super.onResume()
        vm.startDiscovery()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewHolder = null
    }

    private class ViewHolder(private val view: View, val zeroconfAdapter: ZeroconfAdapter) {
        val baseUrlEditText: EditText = view.findViewById(R.id.et_instance_base_url)
        val discoveryResult: ResultIconView = view.findViewById(R.id.view_discovery_result)
        val continueButton: Button = view.findViewById(R.id.btn_continue)
        val zeroconfHelper: TextView = view.findViewById(R.id.lbl_zeroconf_helper)

        val zeroconfRecyclerView: RecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_zeroconf).apply {
            adapter = zeroconfAdapter
            layoutManager = LinearLayoutManager(view.context)
        }

        val navController: NavController
            get() = view.findNavController()
    }

    companion object {
        fun newInstance() = HostSetupFragment()
    }
}
