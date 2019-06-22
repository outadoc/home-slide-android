package fr.outadoc.quickhass

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetBehaviorFix<V : View> : BottomSheetBehavior<V> {

    constructor() : super()
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onApplyWindowInsets(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        insets: WindowInsetsCompat
    ): WindowInsetsCompat {
        setPeekHeight(peekHeight, false)
        return super.onApplyWindowInsets(coordinatorLayout, child, insets)
    }
}