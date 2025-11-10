package com.example.airdry.util.operational_states

sealed class  WorkDataFiltersDTOState {
    data class IncludeInactiveWorkers(val newValue : Boolean) : WorkDataFiltersDTOState()
    object  FilterDate : WorkDataFiltersDTOState()
    object  FilterSection : WorkDataFiltersDTOState()
    object  FilterName : WorkDataFiltersDTOState()
    object  FilterDepartment : WorkDataFiltersDTOState()
    object  FilterSurname : WorkDataFiltersDTOState()
    object  FilterPhone : WorkDataFiltersDTOState()
    object  FilterSecondaryPhone : WorkDataFiltersDTOState()
    data class FilterForMeOnly(val newValue:Boolean): WorkDataFiltersDTOState()

}