package com.example.airdry.domain.use_cases.WorkManagement

import android.util.Log
import com.example.airdry.data.dto.Work.CreateWorkDoneDTO
import com.example.airdry.domain.REST_API.interfaces.WorkApi
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WriteWorkUpdatesUseCase @Inject constructor(
private val workDoneRepository: WorkDoneRepository,
private  val workApi : WorkApi
) {

    operator  fun invoke () : Flow<DownloadState> = flow{
        try {
            emit(DownloadState.Loading)
            val cachedUpdateswork : List<CreateWorkDoneDTO> =getCachedWork()
            writeWorkDoneUpdates(cachedUpdateswork)
            workDoneRepository.unCacheForUpdateAllWork()
            emit(DownloadState.Success("updated work data successfully"))
        }catch (e: Exception){
            Log.d("WriteWorkUpdatesUseCase", "failed due to ${e.message}")
            emit(DownloadState.Error("failed to write work updates ${e.message}"))
        }
    }
    private suspend fun getCachedWork () :List<CreateWorkDoneDTO>{
        try {
            return  workDoneRepository.getCachedUpdateWork().map { it.generateCreateWorkDoneDTO()}
        }catch (e: Exception){
            Log.d("getCachedForUpdate", "failed due to ${e.message}")
            return emptyList()
        }
    }
    private suspend fun writeWorkDoneUpdates(workData : List<CreateWorkDoneDTO>){
        try {
            workData.forEach {
                workApi.updateWork(it)
            }
        }catch (e: Exception){
            Log.d("writeWorkDoneUpdates", "failed due to ${e.message}")
            throw e
        }
    }
}