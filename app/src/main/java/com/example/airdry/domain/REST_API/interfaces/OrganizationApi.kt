package com.example.airdry.domain.REST_API.interfaces

import com.example.airdry.data.dto.Organization.CreateDptDTO
import com.example.airdry.data.dto.Organization.CreateOrgDTO
import com.example.airdry.data.dto.Organization.CreateSectDTO
import com.example.airdry.data.dto.Organization.GetOrgDTO
import com.example.airdry.data.dto.Organization.OrganizationResponse
import com.example.airdry.util.operational_states.ApiResponse
import java.util.UUID

interface OrganizationApi {
    suspend fun getAllDept():List<CreateOrgDTO>
    suspend fun getAllSect(): List<CreateSectDTO>
    suspend fun getSectionByDepartmentId(departmentId: UUID): List<CreateSectDTO>?
    suspend  fun getOrganizationWithNestedDepartments():List<OrganizationResponse>
    suspend fun createOrg(req: CreateOrgDTO,logo: ByteArray?): ApiResponse
    suspend fun getOrgById(orgId: UUID): GetOrgDTO
    suspend fun updateOrg(req: CreateOrgDTO,logo: ByteArray?):ApiResponse
    suspend fun  createSect(req: CreateSectDTO): ApiResponse
    suspend fun  createDepartment(req: CreateDptDTO) : ApiResponse
    suspend fun  updateDepartment(req:CreateDptDTO): ApiResponse
    suspend fun  createSection(req: CreateSectDTO): ApiResponse
    suspend fun  updateSection(req: CreateSectDTO): ApiResponse
    suspend fun  deleteSection(id : UUID):ApiResponse
    suspend fun  deleteDepartment(id : UUID):ApiResponse
}