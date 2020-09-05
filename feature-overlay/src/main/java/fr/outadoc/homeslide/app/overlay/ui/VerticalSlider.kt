package fr.outadoc.homeslide.app.overlay.ui

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Box
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Interaction
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.Strings
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.animation.defaultFlingConfig
import androidx.compose.foundation.animation.fling
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderConstants.InactiveTrackColorAlpha
import androidx.compose.material.SliderConstants.TickColorAlpha
import androidx.compose.material.Surface
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
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
import androidx.compose.ui.util.annotation.IntRange
import androidx.compose.ui.util.format
import androidx.compose.ui.util.lerp
import androidx.ui.tooling.preview.Preview
import kotlin.math.abs
import kotlin.math.roundToInt

@Preview
@Composable
fun VerticalSliderPreview() {
    VerticalSlider(
        value = 0.5f,
        onValueChange = {},
        thumbColor = MaterialTheme.colors.primaryVariant
    )
}

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeEnd: () -> Unit = {},
    thumbColor: Color = MaterialTheme.colors.primary,
    activeTrackColor: Color = MaterialTheme.colors.primary,
    inactiveTrackColor: Color = activeTrackColor.copy(alpha = InactiveTrackColorAlpha),
    activeTickColor: Color = MaterialTheme.colors.onPrimary.copy(alpha = TickColorAlpha),
    inactiveTickColor: Color = activeTrackColor.copy(alpha = TickColorAlpha)
) {
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val position = remember(valueRange, steps) {
        SliderPosition(
            initial = value,
            valueRange = valueRange,
            steps = steps,
            animatedClock = clock,
            onValueChange = onValueChange
        )
    }
    position.onValueChange = onValueChange
    position.scaledValue = value
    WithConstraints(modifier.sliderSemantics(value, position, onValueChange, valueRange, steps)) {
        val maxPx = constraints.maxWidth.toFloat()
        val minPx = 0f
        position.setBounds(minPx, maxPx)

        val flingConfig = SliderFlingConfig(position, position.anchorsPx)
        val gestureEndAction = { velocity: Float ->
            if (flingConfig != null) {
                position.holder.fling(velocity, flingConfig) { reason, endValue, _ ->
                    if (reason != AnimationEndReason.Interrupted) {
                        position.holder.snapTo(endValue)
                        onValueChangeEnd()
                    }
                }
            } else {
                onValueChangeEnd()
            }
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
            orientation = Orientation.Horizontal,
            interactionState = interactionState,
            onDragStopped = gestureEndAction,
            startDragImmediately = position.holder.isRunning,
            onDrag = { position.holder.snapTo(position.holder.value + it) }
        )
        val coerced = value.coerceIn(position.startValue, position.endValue)
        val fraction = calcFraction(position.startValue, position.endValue, coerced)
        SliderImpl(
            positionFraction = fraction,
            tickFractions = position.tickFractions,
            thumbColor = thumbColor,
            trackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor,
            activeTickColor = activeTickColor,
            inactiveTickColor = inactiveTickColor,
            width = maxPx,
            interactionState = interactionState,
            modifier = press.then(drag)
        )
    }
}

@Composable
private fun SliderImpl(
    positionFraction: Float,
    tickFractions: List<Float>,
    thumbColor: Color,
    trackColor: Color,
    inactiveTrackColor: Color,
    activeTickColor: Color,
    inactiveTickColor: Color,
    width: Float,
    interactionState: InteractionState,
    modifier: Modifier
) {
    val widthDp = with(DensityAmbient.current) {
        width.toDp()
    }
    Stack(modifier.then(DefaultSliderConstraints)) {
        val thumbSize = ThumbRadius * 2
        val offset = (widthDp - thumbSize) * positionFraction
        val center = Modifier.gravity(Alignment.CenterStart)

        val trackStrokeWidth: Float
        val thumbPx: Float
        with(DensityAmbient.current) {
            trackStrokeWidth = TrackHeight.toPx()
            thumbPx = ThumbRadius.toPx()
        }
        Track(
            modifier = center.fillMaxSize(),
            color = trackColor,
            inactiveColor = inactiveTrackColor,
            activeTickColor = activeTickColor,
            inactiveTickColor = inactiveTickColor,
            positionFraction = positionFraction,
            tickFractions = tickFractions,
            thumbPx = thumbPx,
            trackStrokeWidth = trackStrokeWidth
        )
        Box(center.padding(start = offset)) {
            val elevation = if (
                Interaction.Pressed in interactionState || Interaction.Dragged in interactionState
            ) {
                ThumbPressedElevation
            } else {
                ThumbDefaultElevation
            }
            Surface(
                shape = CircleShape,
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
    activeTickColor: Color,
    inactiveTickColor: Color,
    positionFraction: Float,
    tickFractions: List<Float>,
    thumbPx: Float,
    trackStrokeWidth: Float
) {
    Canvas(modifier) {
        val sliderLeft = Offset(thumbPx, center.y)
        val sliderRight = Offset(size.width - thumbPx, center.y)
        val sliderStart = sliderLeft
        val sliderEnd = sliderRight
        drawLine(
            inactiveColor,
            sliderStart,
            sliderEnd,
            trackStrokeWidth,
            StrokeCap.Round
        )
        val sliderValue = Offset(
            sliderStart.x + (sliderEnd.x - sliderStart.x) * positionFraction,
            center.y
        )

        drawLine(color, sliderStart, sliderValue, trackStrokeWidth, StrokeCap.Round)
        tickFractions.groupBy { it > positionFraction }.forEach { (afterFraction, list) ->
            drawPoints(
                list.map {
                    Offset(lerp(sliderStart, sliderEnd, it).x, center.y)
                },
                PointMode.Points,
                if (afterFraction) inactiveTickColor else activeTickColor,
                trackStrokeWidth,
                StrokeCap.Round
            )
        }
    }
}

// Scale x1 from a1..b1 range to a2..b2 range
private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
    lerp(a2, b2, calcFraction(a1, b1, x1))

// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

@Composable
private fun SliderFlingConfig(
    value: SliderPosition,
    anchors: List<Float>
): FlingConfig? {
    return if (anchors.isEmpty()) {
        null
    } else {
        val adjustTarget: (Float) -> TargetAnimation? = { _ ->
            val now = value.holder.value
            val point = anchors.minByOrNull { abs(it - now) }
            val adjusted = point ?: now
            TargetAnimation(adjusted, SliderToTickAnimation)
        }
        defaultFlingConfig(adjustTarget = adjustTarget)
    }
}

private fun Modifier.sliderSemantics(
    value: Float,
    position: SliderPosition,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0
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
        setProgress(action = { setSliderProgress(it, coerced, position, onValueChange, steps) })

        // TODO(b/157692376) Remove accessibility scroll actions in Slider when
        //  talkback is fixed
        var increment = (position.endValue - position.startValue) / AccessibilityStepsCount
        if (steps > 0) {
            increment = (position.endValue - position.startValue) / (steps + 1)
        }
        if (coerced < position.endValue) {
            @Suppress("DEPRECATION")
            scrollForward(action = {
                setSliderProgress(coerced + increment, coerced, position, onValueChange, steps)
            })
        }
        if (coerced > position.startValue) {
            @Suppress("DEPRECATION")
            scrollBackward(action = {
                setSliderProgress(coerced - increment, coerced, position, onValueChange, steps)
            })
        }
    }
}

private fun setSliderProgress(
    targetValue: Float,
    currentValue: Float,
    position: SliderPosition,
    onValueChange: (Float) -> Unit,
    @IntRange(from = 0) steps: Int = 0
): Boolean {
    var newValue = targetValue.coerceIn(position.startValue, position.endValue)
    if (steps >= 0) {
        val anchorsValue = position.tickFractions.map {
            lerp(position.startValue, position.endValue, it)
        }
        val point = anchorsValue.minByOrNull { abs(it - newValue) }
        newValue = point ?: newValue
    }
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
 * Internal state for [Slider] that represents the Slider value, its bounds and optional amount of
 * steps evenly distributed across the Slider range.
 *
 * @param initial initial value for the Slider when created. If outside of range provided,
 * initial position will be coerced to this range
 * @param valueRange range of values that Slider value can take
 * @param steps if greater than 0, specifies the amounts of discrete values, evenly distributed
 * between across the whole value range. If 0, slider will behave as a continuous slider and allow
 * to choose any value from the range specified
 */
private class SliderPosition(
    initial: Float = 0f,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    animatedClock: AnimationClockObservable,
    var onValueChange: (Float) -> Unit
) {

    internal val startValue: Float = valueRange.start
    internal val endValue: Float = valueRange.endInclusive

    init {
        require(steps >= 0) {
            "steps should be >= 0"
        }
    }

    internal var scaledValue: Float = initial
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

    internal fun setBounds(min: Float, max: Float) {
        if (startPx == min && endPx == max) return
        val newValue = scale(startPx, endPx, holder.value, min, max)
        startPx = min
        endPx = max
        holder.setBounds(min, max)
        anchorsPx = tickFractions.map {
            lerp(startPx, endPx, it)
        }
        holder.snapTo(newValue)
    }

    internal val tickFractions: List<Float> =
        if (steps == 0) emptyList() else List(steps + 2) { it.toFloat() / (steps + 1) }

    internal var anchorsPx: List<Float> = emptyList()
        private set

    @Suppress("UnnecessaryLambdaCreation")
    internal val holder =
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

// Internal to be referred to in tests
internal val ThumbRadius = 10.dp
private val ThumbRippleRadius = 24.dp
private val ThumbDefaultElevation = 1.dp
private val ThumbPressedElevation = 6.dp

// Internal to be referred to in tests
internal val TrackHeight = 32.dp
private val SliderHeight = 48.dp
private val SliderMinWidth = 144.dp // TODO: clarify min width
private val DefaultSliderConstraints =
    Modifier.preferredWidthIn(minWidth = SliderMinWidth)
        .preferredHeightIn(maxHeight = SliderHeight)

private val SliderToTickAnimation = TweenSpec<Float>(durationMillis = 100)
