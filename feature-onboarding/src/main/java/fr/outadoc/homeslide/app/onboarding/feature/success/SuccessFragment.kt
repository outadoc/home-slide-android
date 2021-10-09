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

package fr.outadoc.homeslide.app.onboarding.feature.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import fr.outadoc.homeslide.app.onboarding.R
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentSuccessBinding
import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import io.uniflow.android.livedata.onEvents
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import org.koin.androidx.viewmodel.ext.android.viewModel

class SuccessFragment : Fragment() {

    private val vm: SuccessViewModel by viewModel()

    private var binding: FragmentSuccessBinding? = null
    private val confettiColors = intArrayOf(
        R.color.confetti1,
        R.color.confetti2,
        R.color.confetti3,
        R.color.confetti4
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSuccessBinding.inflate(inflater, container, false).apply {
            buttonContinue.setOnClickListener {
                vm.onContinueClicked()
            }
        }

        onEvents(vm) { event ->
            binding?.apply {
                when (event) {
                    SuccessViewModel.ShowConfettiEvent -> {
                        konfetti.doOnLayout { showConfetti() }
                    }
                    NavigationEvent.Next -> {
                        navController.navigate(SuccessFragmentDirections.finishOnboardingAction())
                        activity?.finish()
                    }
                    NavigationEvent.Back -> navController.navigateUp()
                    else -> Unit
                }
            }
        }

        vm.onOpen()

        return binding?.root
    }

    private fun showConfetti() {
        binding?.konfetti?.apply {
            build()
                .addColors(confettiColors.map { ContextCompat.getColor(context, it) })
                .setDirection(0.0, 360.0)
                .setSpeed(4f, 7f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square, Shape.Circle)
                .addSizes(Size(12), Size(16, 6f))
                .setPosition(-50f, width + 50f, -50f, -50f)
                .streamFor(300, 2000L)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private val FragmentSuccessBinding.navController: NavController
        get() = root.findNavController()
}
