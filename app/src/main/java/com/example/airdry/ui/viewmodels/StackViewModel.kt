package com.example.airdry.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.use_cases.StackManagement.DownloadStackDataUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StackViewModel @Inject constructor(
    private val stackRepo: StackRepository,
    private val downloadStackDataUseCase: DownloadStackDataUseCase
):ViewModel()
{
    private  val _allStacks = MutableStateFlow<List<NestedStack>>(emptyList())
    val allStack : StateFlow<List<NestedStack>> get() = _allStacks
    private val _loadedStack  = MutableStateFlow<NestedStack?>(null)
    val loadedStack : StateFlow<NestedStack?> get() = _loadedStack


    private  val _isSyncing  = MutableStateFlow(true)
    val isSyncing : StateFlow<Boolean> get() = _isSyncing


    private  val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()





    fun getAllStacks(){
        viewModelScope.launch {
            _isSyncing.value = true
            stackRepo.getAllNestedStacks()?.let{
                _allStacks.value = it
                delay(2000)
                _isSyncing.value = false

            }
        }
    }
    fun loadStackById(id : UUID){
        viewModelScope.launch {
            stackRepo.getNestedStackById(id)?.get(0).let{
                _loadedStack.value = it
            }
        }
    }
    fun updateFilteredStacks(searchTerm: String)
    {
        viewModelScope.launch {
            _isSyncing.value =true
            stackRepo.getFilteredStacks(searchTerm).let{
                if(it==null){
                    _isSyncing.value = false
                    getAllStacks()
                }
                else{
                    _allStacks.value =it
                    delay(2000)
                    _isSyncing.value = false

                }
            }

        }
    }

}