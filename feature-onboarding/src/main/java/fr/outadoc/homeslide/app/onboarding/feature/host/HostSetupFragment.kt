package fr.outadoc.homeslide.app.onboarding.feature.host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentSetupHostBinding
import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import fr.outadoc.homeslide.util.view.addTextChangedListener
import io.uniflow.androidx.flow.onEvents
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.viewModel

class HostSetupFragment : Fragment() {

    private val vm: HostSetupViewModel by viewModel()

    private var binding: FragmentSetupHostBinding? = null
    private val zeroconfAdapter = ZeroconfAdapter(
        onItemClick = {
            vm.onZeroconfHostSelected(it)
        }, onItemCountChanged = {
            binding?.recyclerViewZeroconf?.scheduleLayoutAnimation()
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupHostBinding.inflate(inflater, container, false).apply {
            editTextInstanceBaseUrl.addTextChangedListener { s ->
                vm.onInstanceUrlChanged(s)
            }

            editTextInstanceBaseUrl.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        vm.onLoginClicked()
                        true
                    }
                    else -> false
                }
            }

            buttonContinue.setOnClickListener {
                vm.onLoginClicked()
            }

            recyclerViewZeroconf.apply {
                adapter = zeroconfAdapter
                layoutManager = LinearLayoutManager(root.context)
            }
        }

        onStates(vm) { state ->
            binding?.apply {
                if (state is HostSetupViewModel.State) {
                    resultIconViewDiscoveryResult.state = when (state) {
                        is HostSetupViewModel.State.Loading -> ResultIconView.State.LOADING
                        is HostSetupViewModel.State.Error -> ResultIconView.State.ERROR
                        is HostSetupViewModel.State.Success -> ResultIconView.State.SUCCESS
                        else -> ResultIconView.State.NONE
                    }

                    textViewZeroconfHelper.isInvisible = state.discoveredInstances.isEmpty()
                    zeroconfAdapter.submitList(state.discoveredInstances.toList())

                    buttonContinue.apply {
                        when (state) {
                            is HostSetupViewModel.State.Success -> {
                                isEnabled = true
                                alpha = 1f
                            }
                            else -> {
                                isEnabled = false
                                alpha = 0.6f
                            }
                        }
                    }
                }
            }
        }

        onEvents(vm) { event ->
            binding?.apply {
                when (val data = event.take()) {
                    is HostSetupViewModel.Event.SetInstanceUrl -> {
                        editTextInstanceBaseUrl.setText(data.instanceUrl)
                    }
                    is NavigationEvent.Url -> navigate(
                        HostSetupFragmentDirections.startOAuthFlowAction(data.url)
                    )
                    is NavigationEvent.Back -> navController.navigateUp()
                    else -> Unit
                }
            }
        }

        vm.onOpen()

        return binding?.root
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
}
