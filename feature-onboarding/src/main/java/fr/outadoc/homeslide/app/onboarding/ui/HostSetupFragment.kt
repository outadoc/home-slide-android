package fr.outadoc.homeslide.app.onboarding.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentSetupHostBinding
import fr.outadoc.homeslide.app.onboarding.extensions.toViewStatus
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.app.onboarding.ui.HostSetupFragmentDirections.Companion.actionSetupHostFragmentToAuthenticationCustomTabs
import fr.outadoc.homeslide.app.onboarding.ui.HostSetupFragmentDirections.Companion.actionSetupHostFragmentToSetupShortcutFragment
import fr.outadoc.homeslide.app.onboarding.vm.HostSetupViewModel
import fr.outadoc.homeslide.util.view.addTextChangedListener
import org.koin.android.viewmodel.ext.android.viewModel

class HostSetupFragment : Fragment() {

    private val vm: HostSetupViewModel by viewModel()

    private var binding: FragmentSetupHostBinding? = null
    private val zeroconfAdapter = ZeroconfAdapter(
        onItemClick = {
            vm.onZeroconfHostSelected(it)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupHostBinding.inflate(inflater, container, false).apply {
            etInstanceBaseUrl.addTextChangedListener { s ->
                vm.onInstanceUrlChanged(s)
            }

            btnContinue.setOnClickListener {
                vm.onLoginClicked()
            }

            recyclerViewZeroconf.apply {
                adapter = zeroconfAdapter
                layoutManager = LinearLayoutManager(root.context)
            }

            (iconLoading.drawable as? AnimatedVectorDrawable)?.start()
        }

        vm.inputInstanceUrl.observe(viewLifecycleOwner) { instanceUrl ->
            binding?.apply {
                if (etInstanceBaseUrl.text.toString() != instanceUrl) {
                    etInstanceBaseUrl.setText(instanceUrl)
                }
            }
        }

        vm.instanceDiscoveryInfo.observe(viewLifecycleOwner) { discovery ->
            binding?.viewDiscoveryResult?.state = discovery.toViewStatus()
        }

        vm.autoDiscoveredInstances.observe(viewLifecycleOwner) { autoDiscovered ->
            binding?.lblZeroconfHelper?.isInvisible = autoDiscovered.isEmpty()

            zeroconfAdapter.apply {
                submitList(autoDiscovered)
                binding?.recyclerViewZeroconf?.scheduleLayoutAnimation()
            }
        }

        vm.canContinue.observe(viewLifecycleOwner) { canContinue ->
            binding?.btnContinue?.apply {
                isEnabled = canContinue
                alpha = if (canContinue) 1f else 0.6f
            }
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (val dest = it.pop()) {
                NavigationFlow.Next -> {
                    navigate(actionSetupHostFragmentToSetupShortcutFragment())
                }

                NavigationFlow.Back -> binding?.navController?.navigateUp()

                is NavigationFlow.Url -> {
                    navigate(actionSetupHostFragmentToAuthenticationCustomTabs(dest.url))
                }
            }
        }

        vm.state.observe(viewLifecycleOwner) { state ->
            binding?.iconLoading?.isVisible = when (state) {
                HostSetupViewModel.State.Loading -> true
                else -> false
            }
        }

        arguments?.getString("code")?.let { code ->
            // We're coming from a deeplink and we got an authentication code
            vm.onAuthCallback(code)
        }

        return binding!!.root
    }

    private fun navigate(directions: NavDirections) {
        binding?.navController?.navigate(directions)
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
        binding = null
    }

    private val FragmentSetupHostBinding.navController: NavController
        get() = root.findNavController()

    companion object {
        fun newInstance() = HostSetupFragment()
    }
}
