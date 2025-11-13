package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.airdry.ConnectivityObserver
import com.example.airdry.MyConnectivityObserver
import com.example.airdry.auth.SessionManager
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.database.Entities.Work.WorkDone
import com.example.airdry.database.Entities.Work.WorkDoneStack
import com.example.airdry.database.Entities.Work.WorkDoneUser
import com.example.airdry.database.Entities.Work.WorkDoneWithUsersAndStacks
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.domain.workers.UpdateWorkDataWorker
import com.example.airdry.domain.workers.UploadUsersWorker
import com.example.airdry.domain.workers.UploadWorkDataWorker
import com.example.airdry.util.UtilityFunctions.Companion.toUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecordWorkViewModel @Inject constructor(
    private  val application: Application,
    private val workDoneRepo: WorkDoneRepository,
    private val stackRepo: StackRepository,
    private val userManagementRepo: UserRepository,
    private  val sessionManager: SessionManager,
    private  val connectivityObserver: MyConnectivityObserver
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
    private  val _updateWorkDone = MutableStateFlow<WorkDone?>(null)
    val updateWorkDone : StateFlow<WorkDone?> = _updateWorkDone

    val isServerReachable = connectivityObserver.isServerReachable


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
                current.selectedUsers.filter { it.appUser.id != user.appUser.id }
            } else {
                current.selectedUsers + user
            }
            current.copy(selectedUsers = newUsers)
        }
    }

    /**
     * Adds a new stack entry with a specific volume to the list.
     * Sends a UI event if the volume is invalid.
     * @param stack The stack to add.
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
               Log.d("process work", "start insert")
               insertWork(workDone,workDoneUsers,workDoneStacks)
                }catch (e: Exception){
               Log.d("process work", "process work failed ${e.message}")
           }
        }
    }


    fun forcedUpload(){
        val workRequest = OneTimeWorkRequestBuilder<UploadWorkDataWorker>().build()
        val workManager = WorkManager.getInstance(application)

        workManager.enqueueUniqueWork(
            UploadWorkDataWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        viewModelScope.launch {
            try {
                workManager.getWorkInfoByIdLiveData(workRequest.id)
                    .asFlow()
                    .filterNotNull()
                    .map { it.state }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            WorkInfo.State.SUCCEEDED -> {
                                Log.d("upload work", "suck seed")
                            }
                            WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                                Log.d("forcedUpload", "Upload failed or cancelled")
                            }
                            else -> { /* do nothing */ }
                        }
                    }
            }catch (e: Exception){
                Log.d("forcedUpload worker", "failed due to ${e.message}")
            }
        }
    }
    fun forcedUpdate(){
        val workRequest = OneTimeWorkRequestBuilder<UpdateWorkDataWorker>().build()
        val workManager = WorkManager.getInstance(application)

        workManager.enqueueUniqueWork(
            UpdateWorkDataWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        viewModelScope.launch {
            try {
                workManager.getWorkInfoByIdLiveData(workRequest.id)
                    .asFlow()
                    .filterNotNull()
                    .map { it.state }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            WorkInfo.State.SUCCEEDED -> {
                                Log.d("update work", "suck seed")
                            }
                            WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                                Log.d("forced update", "Update failed or cancelled")
                            }
                            else -> { /* do nothing */ }
                        }
                    }
            }catch (e: Exception){
                Log.d("forced Update worker", "failed due to ${e.message}")
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
    fun loadWorkForEdit(workDoneId: UUID?) {
        viewModelScope.launch {
            workDoneId?.let {
                val workDone = workDoneRepo.getWorkDoneWithUsersAndStacksById(it)
                _updateWorkDone.value = workDone?.workDone
                workDone?.let {
                    populateFormState(it)
                }
        }
    }
  }
  private suspend fun  populateFormState(workDone : WorkDoneWithUsersAndStacks){

      val selectedUser  = getSelectedUsers(workDone)
      val selectedStacks = getSelectedStacks(workDone)

      _formState.value = WorkFormState(
          id = workDone.workDone.id,
          workDate = workDone.workDone.workDate.time,
          isEditMode = true,
          selectedUsers = selectedUser,
          selectedStacks =selectedStacks
      )
  }
  private  suspend fun getSelectedUsers(workDone: WorkDoneWithUsersAndStacks):List<NestedAppUser>{
      try {
          val  ok =  workDone.users.map {
              userManagementRepo.getById(it.id)!!
          }
          return ok
      }catch (e: Exception){
          Log.d("getSelectedUsers", "failed due to ${e.message}")
          return emptyList()
      }
  }
  private  suspend fun getSelectedStacks(workDone: WorkDoneWithUsersAndStacks) :List<SelectedStackItem>{
      try {
          val ok :List<SelectedStackItem> = workDone.stacks.map {
              val stack = stackRepo.getNestedStackById(it.id)?.get(0)
              SelectedStackItem(stack = stack!!)
          }
          return  ok
      }catch (e: Exception){
          Log.d("getSelectedStacks", "failed due to ${e.message}")
         return emptyList()
      }
  }
 fun updateExistingWork(state: WorkFormState){
 viewModelScope.launch {
    try {
        Log.d("updateWork", "callled 0")
        var profile : Profile? =null
        val profileId = sessionManager.userId?.toUUID()
        if (profileId!=null)
            profile = userManagementRepo.getProfileById(profileId)
        val listOfStacks: List<NestedStack> = state.selectedStacks.map {
            it.stack
        }
        val workDone = WorkDone(
            id = state.id,
            workDate = Timestamp(state.workDate),
            isPendingUpdate = true,
            isPendingInsert = _updateWorkDone.value?.isPendingInsert ==true,
            capturedAt = _updateWorkDone.value?.capturedAt,
            capturedBy = _updateWorkDone.value?.capturedBy,
            captureById = _updateWorkDone.value?.captureById,
            updatedAt = Timestamp(System.currentTimeMillis()),
            updatedBy = "${profile?.name} ${profile?.surname}"
        )
        val workDoneUsers = generateWorkDoneUsers(state.selectedUsers,workDone.id)
        val workDoneStacks = generateWorkDoneStacks(listOfStacks, workDone.id)
        updateWork(workDone, workDoneUsers, workDoneStacks)
        Log.d("updateWork", "finished ")
    }catch (e: Exception){
        Log.d("updateExistingWork", "failed due to ${e.message}")
    }
    }
  }

    private suspend fun updateWork(
        workDone: WorkDone,
        workDoneUsers: List<WorkDoneUser>,
        workDoneStacks: List<WorkDoneStack>
    ) {
        try {
            Log.d("updateWork","start users ${workDoneUsers.size} stacks ${workDoneStacks.size} workDone ${workDone.isPendingUpdate}")
            workDoneRepo.updateWork(workDone)
            workDoneRepo.deleteWorkDoneChildren(workDone.id)
            workDoneUsers.forEach {
                workDoneRepo.createWorkDoneUser(it)
            }
            workDoneStacks.forEach {
                workDoneRepo.createWorkDoneStack(it)
            }
        Log.d("updateWork","start finish ${workDoneUsers.size} stacks ${workDoneStacks.size} workDone ${workDone.isPendingUpdate}")
        }catch (e: Exception){
            Log.d("updateWork", "failed due to ${e.message}")
        }
    }
    fun deleteWork(workDoneId : UUID){
        viewModelScope.launch {
            workDoneRepo.deleteWorkById(workDoneId)
        }
    }
}
data class WorkFormState(
    val id: UUID = UUID.randomUUID(),
    val workDate: Long = System.currentTimeMillis(),
    val isEditMode: Boolean = false,
    val selectedUsers: List<NestedAppUser> = emptyList(),
    val selectedStacks: List<SelectedStackItem> = emptyList()
)

data class SelectedStackItem(
    val stack: NestedStack,
    val uniqueId: UUID = UUID.randomUUID()
)
