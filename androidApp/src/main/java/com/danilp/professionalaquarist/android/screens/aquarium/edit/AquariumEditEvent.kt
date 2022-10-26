package com.danilp.professionalaquarist.android.screens.aquarium.edit

sealed interface AquariumEditEvent {
    object DeleteButtonPressed : AquariumEditEvent
    object InsertButtonPressed : AquariumEditEvent
    data class NameChanged(val name: String) : AquariumEditEvent
    data class MinTemperatureChanged(val temperature: String) : AquariumEditEvent
    data class MaxTemperatureChanged(val temperature: String) : AquariumEditEvent
    data class LitersChanged(val liters: String) : AquariumEditEvent
    data class MinPhChanged(val ph: String) : AquariumEditEvent
    data class MaxPhChanged(val ph: String) : AquariumEditEvent
    data class MinGhChanged(val gh: String) : AquariumEditEvent
    data class MaxGhChanged(val gh: String) : AquariumEditEvent
    data class MinKhChanged(val kh: String) : AquariumEditEvent
    data class MaxKhChanged(val kh: String) : AquariumEditEvent
    data class MinKChanged(val k: String) : AquariumEditEvent
    data class MaxKChanged(val k: String) : AquariumEditEvent
    data class MinNO3Changed(val no3: String) : AquariumEditEvent
    data class MaxNO3Changed(val no3: String) : AquariumEditEvent
    data class MinFeChanged(val fe: String) : AquariumEditEvent
    data class MaxFeChanged(val fe: String) : AquariumEditEvent
    data class MinCaChanged(val ca: String) : AquariumEditEvent
    data class MaxCaChanged(val ca: String) : AquariumEditEvent
    data class MinMgChanged(val mg: String) : AquariumEditEvent
    data class MaxMgChanged(val mg: String) : AquariumEditEvent
    data class MinPO4Changed(val po4: String) : AquariumEditEvent
    data class MaxPO4Changed(val po4: String) : AquariumEditEvent
    data class MinAmmoniaChanged(val ammonia: String) : AquariumEditEvent
    data class MaxAmmoniaChanged(val ammonia: String) : AquariumEditEvent
    data class MinIlluminationChanged(val illumination: String) : AquariumEditEvent
    data class MinCO2Changed(val co2: String) : AquariumEditEvent
    data class DescriptionChanged(val description: String) : AquariumEditEvent
    data class ImagePicked(val imageUri: String) : AquariumEditEvent
}
