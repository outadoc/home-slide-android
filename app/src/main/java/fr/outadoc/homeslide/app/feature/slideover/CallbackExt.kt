package fr.outadoc.homeslide.app.feature.slideover

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.callbacks(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        when {
            parentFragment is T -> parentFragment as T
            activity is T -> activity as T
            else -> throw IllegalStateException("callback of type ${T::class.java.name} not found for ${this::class.java.name}")
        }
    }
}
