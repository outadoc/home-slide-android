package fr.outadoc.homeslide.common.extensions

import fr.outadoc.homeslide.logging.KLog
import io.uniflow.core.flow.ActionFunction_T
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.data.UIState

inline fun <reified T : UIState> DataFlow.actionWith(noinline onAction: ActionFunction_T<T>): Unit =
    actionDispatcher.dispatchAction { state ->
        if (state is T) {
            onAction(state)
        } else {
            KLog.e(
                Exception("Uniflow: expected UIState of type ${T::class.java.name} but was ${state.javaClass}")
            )
        }
    }
