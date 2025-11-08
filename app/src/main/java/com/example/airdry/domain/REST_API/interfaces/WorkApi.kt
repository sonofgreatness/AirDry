package com.example.airdry.domain.REST_API.interfaces

import com.example.airdry.data.dto.Common.PaginatedResponse
import com.example.airdry.data.dto.Work.CreateWorkDoneDTO
import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE
import com.example.airdry.util.operational_states.ApiResponse
import java.util.UUID

interface WorkApi {
    suspend  fun  getDailyWorkSummaries(page: Int =DEFAULT_PAGE,
                                        pageSize: Int= DEFAULT_PAGE_SIZE,
                                        filters: WorkDataFiltersDTO): PaginatedResponse<WorkDaySummaryDTO>?
    suspend  fun  createWork(req : CreateWorkDoneDTO): ApiResponse
    suspend fun getWorkById(id : UUID): CreateWorkDoneDTO?
    suspend  fun updateWork (  req:CreateWorkDoneDTO):ApiResponse
    suspend  fun getDailyWorkSummariesBetweenDates(page: Int, pageSize: Int,filters: WorkDataFiltersDTO): PaginatedResponse<WorkDaySummaryDTO>?
    suspend fun  getDailyWorkSummariesByUser(page: Int, pageSize: Int,filters: WorkDataFiltersDTO)
            : PaginatedResponse<WorkDaySummaryDTO>?

}