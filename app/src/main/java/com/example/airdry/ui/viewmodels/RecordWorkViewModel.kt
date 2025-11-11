package com.example.airdry.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.airdry.auth.SessionManager
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.database.Entities.Work.WorkDone
import com.example.airdry.database.Entities.Work.WorkDoneStack
import com.example.airdry.database.Entities.Work.WorkDoneUser
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.domain.use_cases.WorkManagement.UploadWorkDataUseCase
import com.example.airdry.util.UtilityFunctions.Companion.toUUID
import com.example.airdry.util.operational_states.DownloadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecordWorkViewModel @Inject constructor(
    private val workDoneRepo: WorkDoneRepository,
    private val stackRepo: StackRepository,
    private val userManagementRepo: UserRepository,
    private  val sessionManager: SessionManager,
):ViewModel() {

    // Use to provide workers select list
    private val _filteredEmployees = MutableLiveData<List<NestedAppUser>>()
    val filteredEmployees :LiveData<List<NestedAppUser>>
        get() = _filteredEmployees

    // Use  to provide stacks select list
    private  val _filteredStacks = MutableLiveData<List<NestedStack>>()
    val filteredStacks : LiveData<List<NestedStack>>  get() = _filteredStacks

    private val _formState = MutableStateFlow(WorkFormState())
    val formState: StateFlow<WorkFormState> = _formState

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun populateStacksAndWorkerLists(){
      viewModelScope.launch {
          loadAllEmployees()
          loadAllStacks()
      }
    }
    fun updateFilteredEmployees(searchTerm :String){
        viewModelScope.launch{
            userManagementRepo.getFilteredEmployees(searchTerm).let{
                _filteredEmployees.postValue(it)
            }
        }
    }
    fun loadAllEmployees() {
        viewModelScope.launch {
            val all = userManagementRepo.getFilteredEmployees("") ?: emptyList()
            _filteredEmployees.postValue(all)
            Log.d("loadEmployees", " loadEmployees called size -> ${all.size}")
        }
    }

    fun updateFilteredStacks(searchTerm: String)
    {
        viewModelScope.launch {
            stackRepo.getFilteredStacks(searchTerm).let{
                _filteredStacks.postValue(it)
            }

        }
    }
    private suspend fun  loadAllStacks(){
        _filteredStacks.postValue(stackRepo.getAllNestedStacks())
    }
    /**
     * Updates the work date in the form state.
     * @param newDateMillis The new date in milliseconds.
     */
    fun updateWorkDate(newDateMillis: Long) {
        _formState.update {
            it.copy(workDate = newDateMillis)
        }
    }

    /**
     * Toggles the selection status of a worker.
     * If the worker is already selected, they are removed. If not, they are added.
     * @param user The worker to toggle.
     */
    fun toggleUserSelection(user: NestedAppUser) {
        _formState.update { current ->
            val isSelected = current.selectedUsers.any { it.appUser.id == user.appUser.id }
            val newUsers = if (isSelected) {
                // Remove user
                current.selectedUsers.filter { it.appUser.id != user.appUser.id }
            } else {
                // Add user
                current.selectedUsers + user
            }
            current.copy(selectedUsers = newUsers)
        }
    }

    /**
     * Adds a new stack entry with a specific volume to the list.
     * Sends a UI event if the volume is invalid.
     * @param stack The stack to add.
     * @param volumeString The volume as a string, which must be parsed to Double.
     */
    fun addStackSelection(stack: NestedStack) {


        // Create a new SelectedStackItem with a unique ID
        val newStackItem = SelectedStackItem(
            stack = stack,
        )

        _formState.update { current ->
            current.copy(selectedStacks = current.selectedStacks + newStackItem)
        }
    }

    /**
     * Removes a specific stack entry based on its uniqueId.
     * @param item The SelectedStackItem to remove.
     */
    fun removeStackSelection(item: SelectedStackItem) {
        _formState.update { current ->
            val newStacks = current.selectedStacks.filter { it.uniqueId != item.uniqueId }
            current.copy(selectedStacks = newStacks)
        }
    }

    fun recordWork(state: WorkFormState) {
        viewModelScope.launch {
           try {
                Log.d("process work", "start")
               val listOfStacks: List<NestedStack> = state.selectedStacks.map {
                   it.stack
               }
               val workDone =  generateWorkDataFromState(state)
               val workDoneUsers = generateWorkDoneUsers(state.selectedUsers,workDone.id)
               val workDoneStacks = generateWorkDoneStacks(listOfStacks, workDone.id)
               Log.d("process work", "middle")
               insertWork(workDone,workDoneUsers,workDoneStacks)
               Log.d("process work", "end")
           }catch (e: Exception){
               Log.d("process work", "process work failed ${e.message}")
           }
        }

    }
    private  suspend fun  generateWorkDataFromState(state: WorkFormState) :WorkDone{
        var profile:Profile? = null
        sessionManager.userId?.let {
           profile=  userManagementRepo.getProfileById(it.toUUID())
        }

        return  WorkDone(
            id  = UUID.randomUUID(),
            workDate = Timestamp(state.workDate),
            isPendingInsert = true,
            capturedBy = "${profile?.name} ${profile?.surname}",
            captureById =profile?.id,
            capturedAt = Timestamp(System.currentTimeMillis()),
            updatedAt = Timestamp(System.currentTimeMillis())
        )
    }

    private  fun generateWorkDoneUsers(selectedUsers: List<NestedAppUser>,
                                           workDoneId: UUID):List<WorkDoneUser>{

        return selectedUsers.map{
            WorkDoneUser(
                id = UUID.randomUUID(),
                workDoneId =workDoneId,
                appUserId = it.appUser.id,
            )
        }
    }
    private  fun generateWorkDoneStacks(stacks:List<NestedStack>,workDoneId:UUID):List<WorkDoneStack>{
        return  stacks.map { WorkDoneStack(
            id = UUID.randomUUID(),
            workDoneId = workDoneId,
            stackId = it.stack.id
        ) }
    }
    private  suspend fun  insertWork(workDone: WorkDone,
                                     workDoneUsers: List<WorkDoneUser>,
                                     workDoneStacks: List<WorkDoneStack>){
        try {
            workDoneRepo.createWork(workDone)
            workDoneStacks.forEach {
                workDoneRepo.createWorkDoneStack(it)
            }
            workDoneUsers.forEach {
                workDoneRepo.createWorkDoneUser(it)
            }
        }
        catch (e: Exception)
        {
            _uiEvent.send("failed to record work!")
            Log.d("insertWork", "failed it insert work ${e.message}")
        }
    }
    fun clearFormState() {
        _formState.value = WorkFormState()
    }




}
// Data class to represent a single WorkDone entry in the form
data class WorkFormState(
    val id: UUID = UUID.randomUUID(),
    val workDate: Long = System.currentTimeMillis(), // Use Long for DatePicker
    val isEditMode: Boolean = false,
    val selectedUsers: List<NestedAppUser> = emptyList(),
    val selectedStacks: List<SelectedStackItem> = emptyList()
)

data class SelectedStackItem(
    val stack: NestedStack,
    val uniqueId: UUID = UUID.randomUUID()
)
