package com.example.airdry.domain.use_cases

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.example.airdry.R
import com.example.airdry.auth.SessionManager
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.domain.REST_API.interfaces.OrganizationApi
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.util.UtilityFunctions.Companion.toUUID
import javax.inject.Inject

class CreateLogInProfileUseCase@Inject constructor(
    private val app : Application,
    private val userRepository: UserRepository,
    private val userApi: UserApi,
   private val organizationApi: OrganizationApi,
    private val sessionManager: SessionManager
){

    suspend operator fun invoke(){
        Log.d("CreateLogInProfileUseCase","invoke ${sessionManager.userId}")
        sessionManager.userId?.let{
           val user = userApi.findUserResponseById(it.toUUID())
            var myLogo : ByteArray?=null
            user?.organization?.id?.let { id ->
                myLogo = organizationApi.getOrgById(id).logo
            }
            if (myLogo == null) {
                val inputStream = app.resources.openRawResource(R.raw.main_logo)
                myLogo = inputStream.use { it.readBytes() }
            }

            user?.let {user ->
                val profile = Profile(
                    id = user.id!!,
                    name = user.name,
                    surname = user.surname,
                    section = user.section?.displayName?:"",
                    department = user.department?.displayName?:"",
                    organization = user.organization?.displayName?:"",
                    userType = user.userType?.displayName?:"",
                    logo =myLogo,
                    email = user.email!!,
                )
                userRepository.createProfile(profile)
                Toast.makeText(app, "Profile saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

}