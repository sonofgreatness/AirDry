package com.example.airdry.domain.REST_API.interfaces

import com.example.airdry.data.dto.Common.PaginatedResponse
import com.example.airdry.data.dto.Work.CreateStackCodeDTO
import com.example.airdry.data.dto.Work.CreateStackDTO
import com.example.airdry.data.dto.Work.GetStackDTO
import com.example.airdry.util.operational_states.ApiResponse
import java.util.UUID

interface StackApi {
    suspend fun createStackCode(req: CreateStackCodeDTO): ApiResponse
    suspend fun  createStack(req: CreateStackDTO): ApiResponse

    suspend fun updateStackCode(req: CreateStackCodeDTO): ApiResponse
    suspend fun  updateStack(req: CreateStackDTO): ApiResponse

    suspend fun deleteStackById(stackId: UUID): ApiResponse
    suspend fun deleteStackCodeById(stackCodeId: UUID): ApiResponse

    suspend fun getStackById(stackId: UUID): GetStackDTO?
    suspend fun  getStackCodeByID(stackCodeId: UUID): CreateStackCodeDTO?

    suspend fun  getAllStackCodes(): List<CreateStackCodeDTO>
    suspend fun  getAllStacks():List<GetStackDTO>
    suspend  fun getPaginatedStacks(page: Int,pageSize: Int,query:String? =null): PaginatedResponse<GetStackDTO>?
}