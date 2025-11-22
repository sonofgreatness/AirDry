package com.example.airdry.domain.REST_API.implementation

import android.util.Log
import com.example.airdry.data.dto.Common.PaginatedResponse
import com.example.airdry.data.dto.Work.CreateWorkDoneDTO
import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.domain.REST_API.interfaces.WorkApi
import com.example.airdry.util.Constants.Companion.API_BASE_URL
import com.example.airdry.util.operational_states.ApiResponse
import com.example.airdry.util.ConstanstFromShared.Companion.API_URL_WORK_WORK_GET_ALL_DAILY_SUMMARY
import com.example.airdry.util.ConstanstFromShared.Companion.API_URL_WORK_WORK_GET_ALL_DAILY_SUMMARY_BETWEEN_DATES
import com.example.airdry.util.ConstanstFromShared.Companion.API_URL_WORK_WORK_GET_ALL_DAILY_SUMMARY_BETWEEN_DATES_BY_USER
import com.example.airdry.util.ConstanstFromShared.Companion.API_URL_WORK_WORK_REGISTER
import com.example.airdry.util.ConstanstFromShared.Companion.API_URL_WORK_WORK_UPDATE
import com.example.airdry.util.ConstanstFromShared.Companion.PAGE
import com.example.airdry.util.ConstanstFromShared.Companion.PAGE_SIZE
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.util.UUID


class WorkApiImpl(    private  val client: HttpClient
): WorkApi {
    override suspend fun getDailyWorkSummaries(
        page: Int,
        pageSize: Int,
        filters: WorkDataFiltersDTO
    ): PaginatedResponse<WorkDaySummaryDTO>? {
        try{
            val  resp  = client.post("$API_BASE_URL$API_URL_WORK_WORK_GET_ALL_DAILY_SUMMARY"){
                contentType(ContentType.Application.Json)
                url {
                    parameters.append(PAGE, page.toString())
                    parameters.append(PAGE_SIZE, pageSize.toString())
                }
                setBody(filters)
            }
            return resp.body()
        }catch(e: Exception){
            e.printStackTrace()
            println("failed to get workSummaries ${e.message}")
            return  null
        }
    }

    override  suspend  fun  createWork(req : CreateWorkDoneDTO): ApiResponse {
        val resp = client.post("$API_BASE_URL$API_URL_WORK_WORK_REGISTER"){
            contentType(ContentType.Application.Json)
            setBody(req)
        }
        Log.d("createWork", "resp: ${resp.bodyAsText()}")
        return if (resp.status.isSuccess())
            ApiResponse.Success
        else
            ApiResponse.Error(HttpStatusCode.InternalServerError.value,
                "Failed to create work ${resp.bodyAsText()}")
    }
    override suspend fun getWorkById(id : UUID): CreateWorkDoneDTO?{
        val resp = client.get("$API_BASE_URL/work/work/get_work_by_id/${id}"){
            contentType(ContentType.Application.Json)
        }.body<CreateWorkDoneDTO?>()
        return resp
    }
    override  suspend fun updateWork (  req:CreateWorkDoneDTO):ApiResponse{
        val resp = client.post("$API_BASE_URL$API_URL_WORK_WORK_UPDATE"){
            contentType(ContentType.Application.Json)
            setBody(req)
        }
        return if (resp.status.isSuccess())
            ApiResponse.Success
        else
            ApiResponse.Error(HttpStatusCode.InternalServerError.value,
                "Failed to update work ${resp.bodyAsText()}")
    }

    override suspend  fun getDailyWorkSummariesBetweenDates(page: Int, pageSize: Int,filters: WorkDataFiltersDTO): PaginatedResponse<WorkDaySummaryDTO>?
    {
        try{
            println("Get DailyWorkSummaries between dates")
            val  resp  = client.post("$API_BASE_URL$API_URL_WORK_WORK_GET_ALL_DAILY_SUMMARY_BETWEEN_DATES"){
                contentType(ContentType.Application.Json)
                url {
                    parameters.append(PAGE, page.toString())
                    parameters.append(PAGE_SIZE, pageSize.toString())
                }
                setBody(filters)
            }
            println("resp: ${resp.bodyAsText()}")
            return resp.body()
        }catch(e: Exception){
            e.printStackTrace()
            println("failed to get workSummaries  between dates${e.message}")
            return  null
        }
    }
    override suspend fun  getDailyWorkSummariesByUser(page: Int,
                                                      pageSize: Int,filters: WorkDataFiltersDTO)
            : PaginatedResponse<WorkDaySummaryDTO>?{
        try{
            val  resp  = client.post("$API_BASE_URL$API_URL_WORK_WORK_GET_ALL_DAILY_SUMMARY_BETWEEN_DATES_BY_USER"){
                contentType(ContentType.Application.Json)
                url {
                    parameters.append(PAGE, page.toString())
                    parameters.append(PAGE_SIZE, pageSize.toString())
                }
                setBody(filters)
            }
            return resp.body()
        }catch(e: Exception){
            e.printStackTrace()
            return  null
        }
    }
}

