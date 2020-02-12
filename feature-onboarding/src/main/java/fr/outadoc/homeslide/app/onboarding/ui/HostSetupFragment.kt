package fr.outadoc.homeslide.app.onboarding.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.homeslide.app.onboarding.R
import fr.outadoc.homeslide.app.onboarding.extensions.toViewStatus
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.app.onboarding.ui.HostSetupFragmentDirections.Companion.actionSetupHostFragmentToAuthenticationCustomTabs
import fr.outadoc.homeslide.app.onboarding.ui.HostSetupFragmentDirections.Companion.actionSetupHostFragmentToSetupShortcutFragment
import fr.outadoc.homeslide.app.onboarding.ui.HostSetupFragmentDirections.Companion.actionSetupHostFragmentToSuccessFragment
import fr.outadoc.homeslide.app.onboarding.vm.HostSetupViewModel
import fr.outadoc.homeslide.util.view.addTextChangedListener
import org.koin.android.viewmodel.ext.android.viewModel

class HostSetupFragment : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: HostSetupViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setup_host, container, false)

        val adapter = ZeroconfAdapter(
            onItemClick = {
                vm.onZeroconfHostSelected(it)
            }
        )

        viewHolder = ViewHolder(view, adapter).apply {
            baseUrlEditText.addTextChangedListener { s ->
                vm.onInstanceUrlChanged(s)
            }

            continueButton.setOnClickListener {
                vm.onLoginClicked()
            }
        }

        vm.inputInstanceUrl.observe(viewLifecycleOwner) { instanceUrl ->
            viewHolder?.apply {
                if (baseUrlEditText.text.toString() != instanceUrl) {
                    baseUrlEditText.setText(instanceUrl)
                }
            }
        }

        vm.instanceDiscoveryInfo.observe(viewLifecycleOwner) { discovery ->
            viewHolder?.discoveryResult?.state = discovery.toViewStatus()
        }

        vm.autoDiscoveredInstances.observe(viewLifecycleOwner) { autoDiscovered ->
            viewHolder?.zeroconfHelper?.isInvisible = autoDiscovered.isEmpty()

            viewHolder?.zeroconfAdapter?.apply {
                submitList(autoDiscovered)
                viewHolder?.zeroconfRecyclerView?.scheduleLayoutAnimation()
            }
        }

        vm.canContinue.observe(viewLifecycleOwner) { canContinue ->
            viewHolder?.continueButton?.isEnabled = canContinue
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (val dest = it.pop()) {
                NavigationFlow.Next -> {
                    val direction =
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            // If the OS doesn't support quick settings, skip the quick settings step
                            actionSetupHostFragmentToSuccessFragment()
                        } else {
                            actionSetupHostFragmentToSetupShortcutFragment()
                        }

                    navigate(direction)
                }

                NavigationFlow.Back -> viewHolder?.navController?.navigateUp()

                is NavigationFlow.Url -> {
                    navigate(actionSetupHostFragmentToAuthenticationCustomTabs(dest.url))
                }
            }
        }

        vm.state.observe(viewLifecycleOwner) { state ->
            viewHolder?.loadingView?.isVisible = when (state) {
                HostSetupViewModel.State.Loading -> true
                else -> false
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

        val loadingView: View = view.findViewById<View>(R.id.frameLayout_auth_loading).apply {
            findViewById<ImageView>(R.id.icon_loading).apply {
                (drawable as? AnimatedVectorDrawable)?.start()
            }
        }

        val zeroconfRecyclerView: RecyclerView =
            view.findViewById<RecyclerView>(R.id.recyclerView_zeroconf).apply {
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
