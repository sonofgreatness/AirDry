package com.example.airdry.domain.use_cases.WorkManagement

import com.example.airdry.data.dto.Work.CreateWorkDoneDTO
import com.example.airdry.domain.REST_API.interfaces.WorkApi
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import  android.util.Log
class UploadWorkDataUseCase @Inject constructor(
    private val workDoneRepo : WorkDoneRepository,
    private val workApi: WorkApi
) {
    operator  fun invoke() :Flow<DownloadState> =flow{
        try {

            emit(DownloadState.Loading)
            Log.d("upload work", "Loading")
            val cachedWork : List<CreateWorkDoneDTO> = getCachedWork()
            if(cachedWork.isNotEmpty()) {
                Log.d("upload work", "cache not empty")
               uploadCachedData(cachedWork)
                workDoneRepo.unCacheForInsertAllWork()
            }
            emit(DownloadState.Success("work data uploaded successfully"))

        }catch (e: Exception){
            Log.d("upload work", "reachd exception ${e.message}")
            emit(DownloadState.Error("failed to upload work data ${e.message}"))
        }
    }


    private suspend fun getCachedWork() :List<CreateWorkDoneDTO>{
        try {
           return  workDoneRepo.getCachedWork().map {
               it.generateCreateWorkDoneDTO()
           }


        }catch (e: Exception){
            Log.d("upload work", "get Cache exception")
            Log.d("getCachedWork", "failed to get cached work ${e.message}")
            return emptyList()
        }
    }
    private  suspend fun uploadCachedData(cachedWork:List<CreateWorkDoneDTO>){
        try {
            cachedWork.forEach { workApi.createWork(it)
           }
        }catch (e: Exception){
            Log.d("upload work", "upload exception")
            Log.d("uploadCachedData", "failed to upload cached data ${e.message}")
            throw e
        }
    }
}