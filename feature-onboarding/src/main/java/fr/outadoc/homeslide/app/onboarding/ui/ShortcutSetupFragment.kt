package fr.outadoc.homeslide.app.onboarding.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.wearable.intent.RemoteIntent
import fr.outadoc.homeslide.app.onboarding.R
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentSetupShortcutBinding
import fr.outadoc.homeslide.app.onboarding.extensions.startRemoteActivity
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.app.onboarding.ui.ShortcutSetupFragmentDirections.Companion.actionSetupShortcutFragmentToSuccessFragment
import fr.outadoc.homeslide.app.onboarding.vm.ShortcutSetupViewModel
import fr.outadoc.homeslide.util.view.showSnackbar
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class ShortcutSetupFragment : Fragment() {

    private val vm: ShortcutSetupViewModel by viewModel()
    private var binding: FragmentSetupShortcutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupShortcutBinding.inflate(inflater, container, false).apply {
            btnContinue.setOnClickListener {
                vm.onContinueClicked()
            }

            cardViewDeviceControls.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            cardViewQuickSettings.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

            buttonAssistantOpen.setOnClickListener {
                openDefaultAppSettings()
            }

            buttonWearOpen.setOnClickListener {
                openWearPlayStore()
            }
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (it.pop()) {
                NavigationFlow.Next -> binding?.navController?.navigate(
                    actionSetupShortcutFragmentToSuccessFragment()
                )
                NavigationFlow.Back -> binding?.navController?.navigateUp()
                else -> Unit
            }
        }

        return binding!!.root
    }

    private fun openDefaultAppSettings() {
        startActivity(
            Intent(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS
                } else {
                    Settings.ACTION_SETTINGS
                }
            )
        )
    }

    private fun openWearPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse(WEAR_APP_STORE_URI))

        lifecycleScope.launch {
            binding?.scrollViewShortcutSetup.apply {
                when (context?.startRemoteActivity(intent)) {
                    RemoteIntent.RESULT_OK -> showSnackbar(R.string.onboarding_setup_shortcut_wear_success)
                    else -> showSnackbar(R.string.onboarding_setup_shortcut_wear_failure)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private val FragmentSetupShortcutBinding.navController: NavController
        get() = root.findNavController()

    companion object {
        fun newInstance() = ShortcutSetupFragment()

        private const val WEAR_APP_STORE_URI = "market://details?id=fr.outadoc.quickhass"
    }
}
