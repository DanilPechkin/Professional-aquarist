package com.danilp.professionalaquarist.android.screens.in_aquairum.plant.edit

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danilp.professionalaquarist.android.R
import com.danilp.professionalaquarist.domain.plant.ConvertPlantMeasures
import com.danilp.professionalaquarist.domain.plant.Plant
import com.danilp.professionalaquarist.domain.plant.PlantDataSource
import com.danilp.professionalaquarist.domain.use_case.calculation.conversion.alkalinity.AlkalinityMeasure
import com.danilp.professionalaquarist.domain.use_case.calculation.conversion.temperature.TemperatureMeasure
import com.danilp.professionalaquarist.domain.use_case.validation.Validate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PlantEditViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val validate: Validate,
    private val plantDataSource: PlantDataSource,
    private val convertPlantMeasures: ConvertPlantMeasures
) : ViewModel() {

    var state by mutableStateOf(PlantEditState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>("plantId") ?: return@launch

            state = state.copy(
                plant = if (id == -1L) Plant.createEmpty() else
                    plantDataSource.getPlantById(id) ?: Plant.createEmpty()
            )

            val sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.in_aquarium_info_shared_preferences_key),
                Context.MODE_PRIVATE
            )
            state = state.copy(
                plant = state.plant.copy(
                    aquariumId = sharedPreferences.getLong(
                        context.getString(R.string.saved_aquarium_id_key),
                        0
                    )
                ),
                temperatureMeasureCode = sharedPreferences.getInt(
                    context.getString(R.string.temperature_measure_id_key),
                    TemperatureMeasure.Celsius.code
                ),
                alkalinityMeasureCode = sharedPreferences.getInt(
                    context.getString(R.string.alkalinity_measure_id_key),
                    AlkalinityMeasure.DKH.code
                )
            )

            state = state.copy(
                plant = convertPlantMeasures.to(
                    state.plant,
                    state.alkalinityMeasureCode,
                    state.temperatureMeasureCode
                )
            )

            state = state.copy(
                name = state.plant.name ?: "",
                genus = state.plant.genus ?: "",
                minTemperature = (state.plant.minTemperature ?: "").toString(),
                maxTemperature = (state.plant.maxTemperature ?: "").toString(),
                minPh = (state.plant.minPh ?: "").toString(),
                maxPh = (state.plant.maxPh ?: "").toString(),
                minGh = (state.plant.minGh ?: "").toString(),
                maxGh = (state.plant.maxGh ?: "").toString(),
                minKh = (state.plant.minKh ?: "").toString(),
                maxKh = (state.plant.maxKh ?: "").toString(),
                minCO2 = (state.plant.minCO2 ?: "").toString(),
                minIllumination = (state.plant.minIllumination ?: "").toString(),
                description = state.plant.description ?: ""
            )
        }
    }

    fun onEvent(event: PlantEditEvent) {
        when (event) {
            is PlantEditEvent.InsertButtonPressed -> {
                submitData()
            }

            is PlantEditEvent.DeleteButtonPressed -> {
                viewModelScope.launch {
                    if (state.plant.id != null)
                        delete(state.plant.id!!)
                    validationEventChannel.send(ValidationEvent.Success)
                }
            }

            is PlantEditEvent.NameChanged -> {
                state = state.copy(name = event.name)
            }

            is PlantEditEvent.GenusChanged -> {
                state = state.copy(genus = event.genus)
            }

            is PlantEditEvent.MinTemperatureChanged -> {
                state = state.copy(minTemperature = event.temp)
            }

            is PlantEditEvent.MaxTemperatureChanged -> {
                state = state.copy(maxTemperature = event.temp)
            }

            is PlantEditEvent.MinPhChanged -> {
                state = state.copy(minPh = event.ph)
            }

            is PlantEditEvent.MaxPhChanged -> {
                state = state.copy(maxPh = event.ph)
            }

            is PlantEditEvent.MinGhChanged -> {
                state = state.copy(minGh = event.gh)
            }

            is PlantEditEvent.MaxGhChanged -> {
                state = state.copy(maxGh = event.gh)
            }

            is PlantEditEvent.MinKhChanged -> {
                state = state.copy(minKh = event.kh)
            }

            is PlantEditEvent.MaxKhChanged -> {
                state = state.copy(maxKh = event.kh)
            }

            is PlantEditEvent.MinCO2Changed -> {
                state = state.copy(minCO2 = event.co2)
            }

            is PlantEditEvent.MinIlluminationChanged -> {
                state = state.copy(minIllumination = event.illumination)
            }

            is PlantEditEvent.DescriptionChanged -> {
                state = state.copy(description = event.description)
            }

            is PlantEditEvent.ImagePicked -> {
                state = state.copy(
                    plant = state.plant.copy(imageUrl = event.imageUrl)
                )
            }
        }
    }

    private fun insert(plant: Plant) = viewModelScope.launch {
        plantDataSource.insertPlant(plant)
    }

    private fun delete(plantId: Long) = viewModelScope.launch {
        plantDataSource.deletePlantById(plantId)
    }

    private fun submitData() {
        val nameResult = validate.string(state.name)
        val minTemperatureResult = validate.decimal(state.minTemperature, isRequired = true)
        val maxTemperatureResult = validate.decimal(state.maxTemperature, isRequired = true)
        val minPhResult = validate.decimal(state.minPh)
        val maxPhResult = validate.decimal(state.maxPh)
        val minGhResult = validate.decimal(state.minGh)
        val maxGhResult = validate.decimal(state.maxGh)
        val minKhResult = validate.decimal(state.minKh)
        val maxKhResult = validate.decimal(state.maxKh)
        val minCO2Result = validate.decimal(state.minCO2)
        val minIlluminationResult = validate.decimal(state.minIllumination)

        val hasError = listOf(
            nameResult,
            minTemperatureResult,
            maxTemperatureResult,
            minPhResult,
            maxPhResult,
            minGhResult,
            maxGhResult,
            minKhResult,
            maxKhResult,
            minCO2Result,
            minIlluminationResult
        ).any { it.errorCode != null }

        if (hasError) {
            state = state.copy(
                nameErrorCode = nameResult.errorCode,
                minTemperatureErrorCode = minTemperatureResult.errorCode,
                maxTemperatureErrorCode = maxTemperatureResult.errorCode,
                minPhErrorCode = minPhResult.errorCode,
                maxPhErrorCode = maxPhResult.errorCode,
                minGhErrorCode = minGhResult.errorCode,
                maxGhErrorCode = maxGhResult.errorCode,
                minKhErrorCode = minKhResult.errorCode,
                maxKhErrorCode = maxKhResult.errorCode,
                minCO2ErrorCode = minCO2Result.errorCode,
                minIlluminationErrorCode = minIlluminationResult.errorCode
            )
            return
        }

        viewModelScope.launch {
            val isTempCorrect = (state.minTemperature.toDouble() < state.maxTemperature.toDouble())
            val isPhCorrect = (
                (
                    (
                        state.minPh.toDoubleOrNull()
                            ?: 0.0
                        ) < (state.maxPh.toDoubleOrNull() ?: 0.0)
                    )
                )
            val isGhCorrect = (
                (
                    (
                        state.minGh.toDoubleOrNull()
                            ?: 0.0
                        ) < (state.maxGh.toDoubleOrNull() ?: 0.0)
                    )
                )
            val isKhCorrect = (
                (
                    (
                        state.minKh.toDoubleOrNull()
                            ?: 0.0
                        ) < (state.maxKh.toDoubleOrNull() ?: 0.0)
                    )
                )

            if (!isTempCorrect) {
                kotlin.run {
                    val temp = state.minTemperature
                    state = state.copy(minTemperature = state.maxTemperature)
                    state = state.copy(maxTemperature = temp)
                }
            }

            if (!isPhCorrect) {
                kotlin.run {
                    val temp = state.minPh
                    state = state.copy(minPh = state.maxPh)
                    state = state.copy(maxPh = temp)
                }
            }

            if (!isGhCorrect) {
                kotlin.run {
                    val temp = state.minGh
                    state = state.copy(minGh = state.maxGh)
                    state = state.copy(maxGh = temp)
                }
            }

            if (!isKhCorrect) {
                kotlin.run {
                    val temp = state.minKh
                    state = state.copy(minKh = state.maxKh)
                    state = state.copy(maxKh = temp)
                }
            }

            state = state.copy(
                plant = state.plant.copy(
                    name = state.name.ifBlank { null },
                    genus = state.genus.ifBlank { null },
                    minTemperature = state.minTemperature.ifBlank { null }?.toDouble(),
                    maxTemperature = state.maxTemperature.ifBlank { null }?.toDouble(),
                    minPh = state.minPh.ifBlank { null }?.toDouble(),
                    maxPh = state.maxPh.ifBlank { null }?.toDouble(),
                    minGh = state.minGh.ifBlank { null }?.toDouble(),
                    maxGh = state.maxGh.ifBlank { null }?.toDouble(),
                    minKh = state.minKh.ifBlank { null }?.toDouble(),
                    maxKh = state.maxKh.ifBlank { null }?.toDouble(),
                    minIllumination = state.minIllumination.ifBlank { null }?.toDoubleOrNull(),
                    minCO2 = state.minCO2.ifBlank { null }?.toDoubleOrNull(),
                    description = state.description.ifBlank { null }
                )
            )

            state = state.copy(
                plant = convertPlantMeasures.from(
                    state.plant,
                    state.alkalinityMeasureCode,
                    state.temperatureMeasureCode
                )
            )

            insert(state.plant)
            validationEventChannel.send(ValidationEvent.Success)
        }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}
