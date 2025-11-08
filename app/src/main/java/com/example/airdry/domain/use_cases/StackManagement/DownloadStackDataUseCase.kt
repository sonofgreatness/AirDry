package com.example.airdry.domain.use_cases.StackManagement

import android.util.Log
import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.database.Entities.Stack.StackCode
import com.example.airdry.domain.REST_API.interfaces.StackApi
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloadStackDataUseCase @Inject constructor(
    private  val stackApi: StackApi,
    private  val stackRepository: StackRepository
) {
    operator  fun invoke():Flow<DownloadState> =flow{
        try {
          val allStackCode :List<StackCode> =  getStackCodes()
          val allStacks : List<Stack> = getStacks()
          emit(DownloadState.Loading)
            val equalityCheck = allStacks.size == allStackCode.size
            if ((allStackCode.isNotEmpty() && allStacks.isNotEmpty()) && equalityCheck)
            {
                stackRepository.deleteAllStackCodesAndStack()
            }
          allStackCode.forEach {
              stackRepository.createStackCode(it)
          }
          allStacks.forEach {
              stackRepository.createStack(it)
          }
          emit(DownloadState.Success("download  stack data complete"))
        }catch (e:Exception){
            emit(DownloadState.Error("failed to download stack data ${e.message}"))
        }
    }
   private suspend fun getStackCodes():List<StackCode> {
     try {
        val res = stackApi.getAllStackCodes().map{
            it.toStackCode()
        }
        return res
     }catch (e:Exception){
         Log.d("downloadStackData", "failed to download stack codes ${e.message}")
        return emptyList()
     }
   }
   private  suspend fun getStacks() : List<Stack>{
       try{
           val res  = stackApi.getAllStacks().map {it.toStack()}
           return  res
       }
       catch (e: Exception){
           Log.d("downloadStackData", "failed to download stacks ${e.message}")
           return  emptyList()
       }
   }
}