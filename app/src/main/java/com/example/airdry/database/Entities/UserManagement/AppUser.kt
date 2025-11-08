package com.example.airdry.database.Entities.UserManagement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import com.example.airdry.data.dto.User.UserRegisterRequest
import java.sql.Timestamp
import java.util.UUID

@Entity(tableName = "app_user" ,foreignKeys = [
    ForeignKey(
        entity = Organization::class,
        parentColumns = ["id"],
        childColumns = ["organization_id"],
        onDelete = ForeignKey.Companion.SET_NULL
    ),
    ForeignKey(
        entity = Department::class,
        parentColumns = ["id"],
        childColumns = ["department_id"],
        onDelete = ForeignKey.Companion.SET_NULL
    ),
    ForeignKey(
        entity = Section::class,
        parentColumns = ["id"],
        childColumns = ["section_id"],
        onDelete = ForeignKey.Companion.SET_NULL
    ),
    ForeignKey(
        entity = UserType::class,
        parentColumns = ["id"],
        childColumns = ["user_type_id"],
        onDelete = ForeignKey.Companion.SET_NULL
    )],
        indices = [Index("department_id"),Index("organization_id"),
    Index("section_id"),Index("user_type_id"),
            Index(value = ["email"], unique = true)]

)
data class AppUser (
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "surname") val surname: String?,
    @ColumnInfo(name = "national_id") val nationalId: String?,
    @ColumnInfo(name = "phone_number") val phoneNumber: String?,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean? = false,
    @ColumnInfo(name = "is_pending_insert")
    val isPendingInsert : Boolean? = false,
    @ColumnInfo(name = "email")
    val email :String,
    @ColumnInfo(name = "is_approved")
    val isApproved  : Boolean? = false,
    @ColumnInfo(name = "created_at")
    val createdAt : Timestamp? = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name = "updated_at")
    val updatedAt : Timestamp? = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name ="created_by")
    val createdBy : UUID?,
    @ColumnInfo("updated_by")
    val updatedBy: UUID?,
    @ColumnInfo(name = "user_type_id")
    val userTypeId : UUID?,
    @ColumnInfo(name = "organization_id")
    val organizationId : UUID?,
    @ColumnInfo(name = "department_id")
    val departmentId : UUID?,
    @ColumnInfo(name = "section_id")
    val sectionId : UUID?
){
    override fun toString() : String{

        return "$name  $surname $phoneNumber"
    }
    fun printFullname  (): String{
        return "$name  $surname"

    }


    fun toUserRegisterRequest(): UserRegisterRequest{

        return  UserRegisterRequest(
            id = id,
            name =name.toString(),
            surname = surname.toString(),
            phoneNumber = phoneNumber.toString(),
            nationalId = nationalId,
            email = email,
            isActive = isActive ==true,
            isApproved = isActive == true,
            userTypeId = userTypeId,
            organizationId =organizationId ,
            departmentId = departmentId,
            sectionId =sectionId
        )
    }

}