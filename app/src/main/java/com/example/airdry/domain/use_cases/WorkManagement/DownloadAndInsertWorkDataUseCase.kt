package com.example.airdry.domain.use_cases.WorkManagement

import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.domain.REST_API.interfaces.WorkApi
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.Constants.Companion.DEFAULT_OFFSETMONTH_VALUE
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE
import com.example.airdry.util.Constants.Companion.OFFSETMONTH_KEY
import com.example.airdry.util.UtilityFunctions.Companion.generateNowFilterDate
import com.example.airdry.util.UtilityFunctions.Companion.generateOffSetFilterDate
import com.example.airdry.util.operational_states.DownloadState
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import  android.util.Log
import androidx.sqlite.throwSQLiteException

class DownloadAndInsertWorkDataUseCase
@Inject constructor(private  val workApi: WorkApi,
    private  val workDoneRepository: WorkDoneRepository,
    private val settings : Settings)

{
    private val _summaryCurrentPage = MutableStateFlow(1)
    private val _summaryTotalPages = MutableStateFlow(Int.MAX_VALUE)
    private val _summaries = MutableStateFlow<List<WorkDaySummaryDTO>>(emptyList())

    operator fun invoke() : Flow<DownloadState> =flow{
        try {
                val filters = getDateFilters()
            emit(DownloadState.Loading)
            populateSummariesList(filters)
            insertWorkDoneInDB()
            emit(DownloadState.Success("Downloaded work data successfully"))


        }catch (e: Exception){
            emit(DownloadState.Error(message="Failed to download and insert work ${e.message}"))
        }
    }
    private  fun getDateFilters(): WorkDataFiltersDTO{
        val endDate: String = generateNowFilterDate()
        val  offSetMonth:Long  = settings.getLong(OFFSETMONTH_KEY, DEFAULT_OFFSETMONTH_VALUE)
        val startDate :String  = generateOffSetFilterDate(offSetMonth)
        return WorkDataFiltersDTO(filterDate = startDate,
            filterDateEndDate = endDate)
    }
    private suspend fun populateSummariesList(filters: WorkDataFiltersDTO) {
     try  {
        val page = _summaryCurrentPage.value
        if (page > _summaryTotalPages.value) return
        val res = workApi.getDailyWorkSummariesBetweenDates(page, DEFAULT_PAGE_SIZE, filters)
         res?.let {
            if (page <= 1) {
                _summaries.value = it.items
                _summaryTotalPages.value = it.totalPages
            } else {
                _summaries.value += it.items
            }
        }
        _summaryCurrentPage.value++
        populateSummariesList(filters)
     }catch (e: Exception){
         throw Exception()
         Log.d("DownloadAndInsertWorkDataUseCase:populateSummariesList", "failed ${e.message}")
     }
    }
    private suspend fun insertWorkDoneInDB(){
       if(_summaries.value.isNotEmpty())
       {
           try {
               workDoneRepository.deleteAllWork()
               _summaries.value.forEach {
                   val mainData = it.workList
                   mainData.forEach {
                       val workDone = it.getWorkDone()
                       workDoneRepository.createWork(workDone)
                       it.users.forEach { usr ->
                           val workDoneUser = usr.generateWorkDoneUser(workDone.id)
                           workDoneRepository.createWorkDoneUser(workDoneUser)
                       }
                       it.stacks.forEach { stk ->
                           val workDoneStack = stk.generateWorkDoneStack(workDone.id)
                           workDoneRepository.createWorkDoneStack(workDoneStack)
                       }
                   }
               }
           }catch (e: Exception){
              Log.d("DownloadAndInsertWorkDataUseCase:insertWork", "Failed to insert Work in db ${e.message}")
               throwSQLiteException(111,"failed to insert work done ${e.message}")
           }
       }
    }
}