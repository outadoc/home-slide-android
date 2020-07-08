package fr.outadoc.homeslide.app.onboarding.feature.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import fr.outadoc.homeslide.app.onboarding.R
import fr.outadoc.homeslide.app.onboarding.databinding.FragmentSuccessBinding
import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import io.uniflow.androidx.flow.onEvents
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import org.koin.android.viewmodel.ext.android.viewModel

class SuccessFragment : Fragment() {

    private val vm: SuccessViewModel by viewModel()

    private var binding: FragmentSuccessBinding? = null
    private val confettiColors =
        intArrayOf(R.color.lt_yellow, R.color.lt_orange, R.color.lt_purple, R.color.lt_pink)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSuccessBinding.inflate(inflater, container, false).apply {
            btnContinue.setOnClickListener {
                vm.onContinueClicked()
            }
        }

        onEvents(vm) { event ->
            binding?.apply {
                when (event.take()) {
                    SuccessViewModel.ShowConfettiEvent -> {
                        konfetti.doOnNextLayout { showConfetti() }
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
                .setDirection(0.0, 359.0)
                .setSpeed(4f, 7f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
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
