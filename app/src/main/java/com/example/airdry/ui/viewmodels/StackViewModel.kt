package com.example.airdry.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.use_cases.StackManagement.DownloadStackData
import com.example.airdry.util.operational_states.DownloadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StackViewModel @Inject constructor(
    private val stackRepo: StackRepository,
    private val downloadStackData: DownloadStackData
):ViewModel()
{
    private  val _allStacks = MutableStateFlow<List<NestedStack>>(emptyList())
    val allStack : StateFlow<List<NestedStack>> get() = _allStacks
    private val _loadedStack  = MutableStateFlow<NestedStack?>(null)
    val loadedStack : StateFlow<NestedStack?> get() = _loadedStack

    private  val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()



    private suspend fun downloadData() {
        downloadStackData().collect{
            when(it){
                is DownloadState.Error ->_uiEvent.send(it.message)
               is DownloadState.Success ->_uiEvent.send(it.message)
              is DownloadState.Loading -> _uiEvent.send("Waiting to download ...")
            }
        }
    }

    fun getAllStacks(){
        viewModelScope.launch {
            stackRepo.getAllNestedStacks()?.let{
                _allStacks.value = it
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

}