package fr.outadoc.homeslide.app.overlay.ui

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.foundation.Box
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Interaction
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.Strings
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderConstants.InactiveTrackColorAlpha
import androidx.compose.material.Surface
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Radius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.semantics.AccessibilityRangeInfo
import androidx.compose.ui.semantics.accessibilityValue
import androidx.compose.ui.semantics.accessibilityValueRange
import androidx.compose.ui.semantics.scrollBackward
import androidx.compose.ui.semantics.scrollForward
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.format
import androidx.compose.ui.util.lerp
import androidx.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@Preview
@Composable
fun VerticalSliderPreview() {
    VerticalSlider(
        value = 0.7f,
        onValueChange = {}
    )
}

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeEnd: () -> Unit = {},
    thumbColor: Color = MaterialTheme.colors.primaryVariant,
    activeTrackColor: Color = MaterialTheme.colors.primary,
    inactiveTrackColor: Color = activeTrackColor.copy(alpha = InactiveTrackColorAlpha)
) {
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val position = remember(valueRange) {
        SliderPosition(
            initial = value,
            valueRange = valueRange,
            animatedClock = clock,
            onValueChange = onValueChange
        )
    }
    position.onValueChange = onValueChange
    position.scaledValue = value
    WithConstraints(modifier.sliderSemantics(value, position, onValueChange, valueRange)) {
        val maxPx = constraints.maxHeight.toFloat()
        val minPx = 0f
        position.setBounds(minPx, maxPx)

        val gestureEndAction = { _: Float ->
            onValueChangeEnd()
        }

        val interactionState = remember { InteractionState() }

        val press = Modifier.pressIndicatorGestureFilter(
            onStart = { pos ->
                position.holder.snapTo(pos.x)
                interactionState.addInteraction(Interaction.Pressed, pos)
            },
            onStop = {
                gestureEndAction(0f)
                interactionState.removeInteraction(Interaction.Pressed)
            },
            onCancel = {
                interactionState.removeInteraction(Interaction.Pressed)
            }
        )

        val drag = Modifier.draggable(
            orientation = Orientation.Vertical,
            interactionState = interactionState,
            onDragStopped = gestureEndAction,
            startDragImmediately = position.holder.isRunning,
            onDrag = { position.holder.snapTo(position.holder.value + it) }
        )
        val coerced = value.coerceIn(position.startValue, position.endValue)
        val fraction = calcFraction(position.startValue, position.endValue, coerced)
        SliderImpl(
            positionFraction = fraction,
            thumbColor = thumbColor,
            trackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor,
            height = maxPx,
            interactionState = interactionState,
            modifier = press.then(drag)
        )
    }
}

@Composable
private fun SliderImpl(
    positionFraction: Float,
    thumbColor: Color,
    trackColor: Color,
    inactiveTrackColor: Color,
    height: Float,
    interactionState: InteractionState,
    modifier: Modifier
) {
    val heightDp = with(DensityAmbient.current) {
        height.toDp()
    }
    Stack(modifier.then(DefaultSliderConstraints)) {
        val thumbSize = ThumbRadius * 2
        val offset = (heightDp - thumbSize) * positionFraction
        val center = Modifier.gravity(Alignment.BottomCenter)

        val trackWidth: Float
        val thumbPx: Float
        val radiusPx: Float

        with(DensityAmbient.current) {
            trackWidth = TrackWidth.toPx()
            thumbPx = ThumbRadius.toPx()
            radiusPx = TrackRadius.toPx()
        }

        Track(
            modifier = center.fillMaxSize(),
            color = trackColor,
            inactiveColor = inactiveTrackColor,
            positionFraction = positionFraction,
            thumbPx = thumbPx,
            trackWidth = trackWidth,
            trackRadius = Radius(radiusPx)
        )

        Box(center.padding(bottom = offset)) {
            val elevation = if (
                Interaction.Pressed in interactionState || Interaction.Dragged in interactionState
            ) {
                ThumbPressedElevation
            } else {
                ThumbDefaultElevation
            }

            Surface(
                shape = RoundedCornerShape(TrackRadius),
                color = thumbColor,
                elevation = elevation,
                modifier = Modifier.indication(
                    interactionState = interactionState,
                    indication = RippleIndication(
                        radius = ThumbRippleRadius,
                        bounded = false
                    )
                )
            ) {
                Spacer(Modifier.preferredSize(thumbSize, thumbSize))
            }
        }
    }
}

@Composable
private fun Track(
    modifier: Modifier,
    color: Color,
    inactiveColor: Color,
    positionFraction: Float,
    thumbPx: Float,
    trackWidth: Float,
    trackRadius: Radius
) {
    Canvas(modifier) {
        val topLeft = Offset(x = (size.width - trackWidth) / 2, y = 0f)
        val trackSize = Size(height = size.height, width = trackWidth)

        // Main track background
        drawRoundRect(
            color = inactiveColor,
            radius = trackRadius,
            size = trackSize,
            topLeft = topLeft
        )

        // Active track background
        drawRoundRect(
            color = color,
            radius = trackRadius,
            size = trackSize.copy(height = size.height * positionFraction + thumbPx),
            topLeft = topLeft.copy(y = size.height * (1 - positionFraction) - thumbPx)
        )
    }
}

// Scale x1 from a1..b1 range to a2..b2 range
private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
    lerp(a2, b2, calcFraction(a1, b1, x1))

// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

private fun Modifier.sliderSemantics(
    value: Float,
    position: SliderPosition,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
): Modifier {
    val coerced = value.coerceIn(position.startValue, position.endValue)
    val fraction = calcFraction(position.startValue, position.endValue, coerced)
    // We only display 0% or 100% when it is exactly 0% or 100%.
    val percent = when (fraction) {
        0f -> 0
        1f -> 100
        else -> (fraction * 100).roundToInt().coerceIn(1, 99)
    }
    return semantics {
        accessibilityValue = Strings.TemplatePercent.format(percent)
        accessibilityValueRange = AccessibilityRangeInfo(coerced, valueRange)
        setProgress(action = { setSliderProgress(it, coerced, position, onValueChange) })

        // TODO(b/157692376) Remove accessibility scroll actions in Slider when
        //  talkback is fixed
        val increment = (position.endValue - position.startValue) / AccessibilityStepsCount
        if (coerced < position.endValue) {
            @Suppress("DEPRECATION")
            scrollForward(action = {
                setSliderProgress(coerced + increment, coerced, position, onValueChange)
            })
        }
        if (coerced > position.startValue) {
            @Suppress("DEPRECATION")
            scrollBackward(action = {
                setSliderProgress(coerced - increment, coerced, position, onValueChange)
            })
        }
    }
}

private fun setSliderProgress(
    targetValue: Float,
    currentValue: Float,
    position: SliderPosition,
    onValueChange: (Float) -> Unit
): Boolean {
    val newValue = targetValue.coerceIn(position.startValue, position.endValue)
    // This is to keep it consistent with AbsSeekbar.java: return false if no
    // change from current.
    if (newValue == currentValue) {
        return false
    }
    onValueChange(newValue)
    return true
}

// 20 is taken from AbsSeekbar.java.
private const val AccessibilityStepsCount = 20

/**
 * Internal state for [Slider] that represents the Slider value and its bounds.
 *
 * @param initial initial value for the Slider when created. If outside of range provided,
 * initial position will be coerced to this range
 * @param valueRange range of values that Slider value can take
 */
private class SliderPosition(
    initial: Float = 0f,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    animatedClock: AnimationClockObservable,
    var onValueChange: (Float) -> Unit
) {

    val startValue: Float = valueRange.start
    val endValue: Float = valueRange.endInclusive

    var scaledValue: Float = initial
        set(value) {
            val scaled = scale(startValue, endValue, value, startPx, endPx)
            // floating point error due to rescaling
            if ((scaled - holder.value) > floatPointMistakeCorrection) {
                holder.snapTo(scaled)
            }
        }

    private val floatPointMistakeCorrection = (valueRange.endInclusive - valueRange.start) / 100

    private var endPx = Float.MAX_VALUE
    private var startPx = Float.MIN_VALUE

    fun setBounds(min: Float, max: Float) {
        if (startPx == min && endPx == max) return
        val newValue = scale(startPx, endPx, holder.value, min, max)
        startPx = min
        endPx = max
        holder.setBounds(min, max)
        holder.snapTo(newValue)
    }

    @Suppress("UnnecessaryLambdaCreation")
    val holder =
        CallbackBasedAnimatedFloat(
            scale(startValue, endValue, initial, startPx, endPx),
            animatedClock
        ) { onValueChange(scale(startPx, endPx, it, startValue, endValue)) }
}

private class CallbackBasedAnimatedFloat(
    initial: Float,
    clock: AnimationClockObservable,
    var onValue: (Float) -> Unit
) : AnimatedFloat(clock) {

    override var value = initial
        set(value) {
            onValue(value)
            field = value
        }
}

private val ThumbRadius = 24.dp
private val ThumbRippleRadius = 64.dp
private val ThumbDefaultElevation = 1.dp
private val ThumbPressedElevation = 6.dp

private val TrackWidth = 56.dp
private val TrackRadius = 16.dp
private val SliderWidth = 64.dp
private val SliderMinHeight = 96.dp
private val DefaultSliderConstraints =
    Modifier.preferredHeightIn(minHeight = SliderMinHeight)
        .preferredWidthIn(maxWidth = SliderWidth)
