package fr.outadoc.quickhass.feature.onboarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.feature.onboarding.vm.SuccessViewModel
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import org.koin.android.viewmodel.ext.android.viewModel

class SuccessFragment : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: SuccessViewModel by viewModel()

    private val confettiColors =
        intArrayOf(R.color.lt_yellow, R.color.lt_orange, R.color.lt_purple, R.color.lt_pink)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_success, container, false)

        viewHolder = ViewHolder(view).apply {
            continueButton.setOnClickListener {
                vm.onContinueClicked()
            }

            confettiView.doOnNextLayout { confetti() }
        }

        vm.navigateTo.observe(viewLifecycleOwner) {
            when (it.pop()) {
                NavigationFlow.Next -> {
                    viewHolder?.navController?.navigate(R.id.action_successFragment_to_slideOverActivity)
                    activity?.finish()
                }
                NavigationFlow.Back -> viewHolder?.navController?.navigateUp()
                else -> Unit
            }
        }

        return view
    }

    private fun confetti() {
        if (!vm.showShowConfetti) {
            return
        }

        viewHolder?.confettiView?.apply {
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
        viewHolder = null
    }

    private class ViewHolder(private val view: View) {
        val continueButton: Button = view.findViewById(R.id.btn_continue)
        val confettiView: KonfettiView = view.findViewById(R.id.konfetti)

        val navController: NavController
            get() = view.findNavController()
    }

    companion object {
        fun newInstance() = SuccessFragment()
    }
}
