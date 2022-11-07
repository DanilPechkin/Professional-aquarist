package com.danilp.professionalaquarist.android.screens.in_aquairum.calculator.calculators.capacity.flat_back_hex

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.danilp.professionalaquarist.android.screens.top_bar_menu.settings.SharedPrefs
import com.danilp.professionalaquarist.domain.use_case.calculation.aquairum.capacity.CalculateCapacity
import com.danilp.professionalaquarist.domain.use_case.calculation.conversion.capacity.CapacityMeasure
import com.danilp.professionalaquarist.domain.use_case.calculation.conversion.capacity.ConvertLiters
import com.danilp.professionalaquarist.domain.use_case.calculation.conversion.metric.ConvertMeters
import com.danilp.professionalaquarist.domain.use_case.calculation.conversion.metric.MetricMeasure
import com.danilp.professionalaquarist.domain.use_case.validation.Validate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class FlatBackHexCalculatorViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val calculateCapacity: CalculateCapacity,
    private val convertLiters: ConvertLiters,
    private val convertMeters: ConvertMeters,
    private val validate: Validate
) : ViewModel() {

    var state by mutableStateOf(FlatBackHexCalculatorState())

    init {
        val sharedPreferences = context.getSharedPreferences(
            SharedPrefs.InAquariumInfo.key,
            Context.MODE_PRIVATE
        )

        state = state.copy(
            capacityMeasureCode = sharedPreferences.getInt(
                SharedPrefs.CapacityMeasure.key,
                CapacityMeasure.Liters.code
            ),
            metricMeasureCode = sharedPreferences.getInt(
                SharedPrefs.MetricMeasure.key,
                MetricMeasure.Meters.code
            )
        )
    }

    fun onEvent(event: FlatBackHexCalculatorEvent) {
        when (event) {
            FlatBackHexCalculatorEvent.CalculateButtonPressed -> {
                calculateOutputCapacity()
            }

            is FlatBackHexCalculatorEvent.HeightChanged -> {
                state = state.copy(height = event.height)
            }

            is FlatBackHexCalculatorEvent.WidthChanged -> {
                state = state.copy(width = event.width)
            }

            is FlatBackHexCalculatorEvent.FullWidthChanged -> {
                state = state.copy(fullWidth = event.fullWidth)
            }

            is FlatBackHexCalculatorEvent.LengthChanged -> {
                state = state.copy(length = event.length)
            }

            is FlatBackHexCalculatorEvent.FullLengthChanged -> {
                state = state.copy(fullLength = event.fullLength)
            }
        }
    }

    private fun calculateOutputCapacity() {

        val heightResult = validate.decimal(state.height, isRequired = true)
        val widthResult = validate.decimal(state.width, isRequired = true)
        val fullWidthResult = validate.decimal(state.fullWidth, isRequired = true)
        val lengthResult = validate.decimal(state.length, isRequired = true)
        val fullLengthResult = validate.decimal(state.fullLength, isRequired = true)

        val hasError = listOf(
            heightResult,
            widthResult,
            fullWidthResult,
            lengthResult,
            fullLengthResult
        ).any { it.errorCode != null }

        if (hasError) {
            state = state.copy(
                heightErrorCode = heightResult.errorCode,
                widthErrorCode = widthResult.errorCode,
                fullWidthErrorCode = fullWidthResult.errorCode,
                lengthErrorCode = lengthResult.errorCode,
                fullLengthErrorCode = fullLengthResult.errorCode
            )
            return
        }

        state = state.copy(
            outputCapacity = convertLiters.to(
                capacityMeasureCode = state.capacityMeasureCode,
                liters = convertLiters.from(
                    capacityMeasureCode = CapacityMeasure.Milliliters.code,
                    value = calculateCapacity.flatBackHex(
                        height = convertMeters.to(
                            metricMeasureCode = MetricMeasure.Centimeters.code,
                            meters = convertMeters.from(
                                state.metricMeasureCode,
                                state.height.toDouble()
                            ).result
                        ).result,
                        width = convertMeters.to(
                            metricMeasureCode = MetricMeasure.Centimeters.code,
                            meters = convertMeters.from(
                                state.metricMeasureCode,
                                state.width.toDouble()
                            ).result
                        ).result,
                        fullWidth = convertMeters.to(
                            metricMeasureCode = MetricMeasure.Centimeters.code,
                            meters = convertMeters.from(
                                state.metricMeasureCode,
                                state.fullWidth.toDouble()
                            ).result
                        ).result,
                        length = convertMeters.to(
                            metricMeasureCode = MetricMeasure.Centimeters.code,
                            meters = convertMeters.from(
                                state.metricMeasureCode,
                                state.length.toDouble()
                            ).result
                        ).result,
                        fullLength = convertMeters.to(
                            metricMeasureCode = MetricMeasure.Centimeters.code,
                            meters = convertMeters.from(
                                state.metricMeasureCode,
                                state.fullLength.toDouble()
                            ).result
                        ).result,
                    ).result
                ).result
            ).result.toString()
        )
    }
}
