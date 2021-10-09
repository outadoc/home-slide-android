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

package fr.outadoc.homeslide.app.onboarding.feature.authcallback

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentAuthCallbackBinding
import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import io.uniflow.android.livedata.onEvents
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthCallbackFragment : Fragment() {

    private val vm: AuthCallbackViewModel by viewModel()
    private var binding: FragmentAuthCallbackBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthCallbackBinding.inflate(inflater, container, false).apply {
            (iconLoading.drawable as? AnimatedVectorDrawable)?.start()
        }

        onEvents(vm) { event ->
            when (event) {
                NavigationEvent.Next -> {
                    navigate(AuthCallbackFragmentDirections.setupShortcutsAction())
                }
            }
        }

        requireArguments().getString(ARG_CODE)?.let { code ->
            // We're coming from a deeplink and we got an authentication code
            vm.onAuthCallback(code)
        }

        return binding?.root
    }

    private fun navigate(directions: NavDirections) {
        binding?.navController?.navigate(directions)
    }

    private val FragmentAuthCallbackBinding.navController: NavController
        get() = root.findNavController()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val ARG_CODE = "code"
    }
}
