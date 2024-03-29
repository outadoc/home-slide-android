/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.app.onboarding.feature.shortcuts

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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.androidx.wearable.intent.RemoteIntent
import com.google.androidx.wearable.intent.startRemoteActivity
import fr.outadoc.homeslide.app.onboarding.R
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentSetupShortcutBinding
import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import fr.outadoc.homeslide.util.view.showSnackbar
import io.uniflow.android.livedata.onEvents
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShortcutSetupFragment : Fragment() {

    private val vm: ShortcutSetupViewModel by viewModel()
    private var binding: FragmentSetupShortcutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupShortcutBinding.inflate(inflater, container, false).apply {
            buttonContinue.setOnClickListener {
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

        onEvents(vm) { event ->
            binding?.apply {
                when (event) {
                    is NavigationEvent.Next -> navController.navigate(
                        ShortcutSetupFragmentDirections.successAction()
                    )
                    is NavigationEvent.Back -> navController.navigateUp()
                    else -> Unit
                }
            }
        }

        return binding?.root
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
        private const val WEAR_APP_STORE_URI = "market://details?id=fr.outadoc.quickhass"
    }
}
