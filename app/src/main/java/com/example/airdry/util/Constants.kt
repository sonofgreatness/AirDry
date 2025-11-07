package com.example.airdry.util

class Constants {
    companion object {
        const val LOCAL_DATABASE_NAME="AirDryDatabase"
        const val SERVER_PORT = 9090
        const val STACKTYPE_T1_5 = "25x76  1.5 metre"
        const val STACKTYPE_T1_8 = "25x76  1.8 metre"

        const val STACKTYPE_T2_2 = "19x100  1.2 metre"
        const val STACKTYPE_T2_0 = "19x100  1 metre"

        const val STACKTYPE_T3_0 = "1976x76  1 metre"
        const val STACKTYPE_T3_2 = "1976x76  1.2 metre"
        const val STACKTYPE_T3_5 = "1976x76  1.5 metre"

        const val  NOTIFICATION_ID = 1001
       const val  DOWNLOAD_NOTIFICATION_ID= 1002
        const val REQUEST_FILE_PERMISSION = 101
        const val GENERATE_FILE_CHANNEL_DESCRIPTION = "Report generated"
        const val GENERATE_FILE_CHANNEL = "generate_file_channel"
       const val GENERATE_FILE_NOTIFICATION_TITLE = "Generating Excel File"


        const val DOWNLOAD_FILE_CHANNEL = "Download app update channel"
        const val  DOWNLOAD_FILE_CHANNEL_DESCRIPTION = "Download app update"
        const  val DOWNLOAD_FILE_NOTIFICATION_TITLE = "Downloading update"
        const  val  DOWNLOAD_FILE_CHANNEL_DESCRIPTION_MAIN = "Used for foreground notifications downloading app updates."
        const val GENERATE_FILE_NOTIFICATION_DESCRIPTION = "Excel file  generated   in file path AirDry/Reports/Excel"

        const val GITHUB_API = "https://api.github.com/"
        const val GITHUB_REPO_URL = "repos/sonofgreatness/AirDry/releases"
       const val API_BASE_URL = "http://10.185.121.27:$SERVER_PORT"
        const val PRODUCT_KEY = "si_sonofgreatness"
        const val  PRODUCT_KEY_PRODUCT_KEY= "key"
        const val  FAILED_LOGIN = "Failed to login, check credentials"
        const val  FRESH_DATA_KEY = "fresh_data"
        const val CONNECTIVITY_DELAY_TIME =5000L
        const val NOT_LOGGED_IN_ERROR= "User is not logged in"
        const val USER_FOUND_NOT_ERROR = "User not found"
        const val SERVER_OK_TEXT = "Server is up and running"
        const val QUERY_STRING = "queryString"
        const val QUERY_PAGE = "page"
        const val QUERY_PAGE_SIZE = "pageSize"
        const val START_DATE = "startDate"
        const val END_DATE = "endDate"
        const val WORK_DONE_EDIT_GRACE_PERIOD = 7
        const val WORK_DONE_EDIT_GRACE_PERIOD_CAPTURER = 2
        const val  DEFAULT_PAGE_SIZE =20
        const val  DEFAULT_PAGE_SIZE_BIG =50
        const val  DEFAULT_PAGE =1
        const val DOWNLOAD_USERS_DONE ="Downloaded workers"
        const val DOWNLOAD_USERS_FAILED ="Failed to download workers"
        const val DOWNLOAD_USER_TYPES_DONE ="Downloaded user types"
        const val DOWNLOAD_USER_TYPES_FAILED ="Failed to download user types"
        const val DOWNLOAD_DEPARTMENTS_DONE ="Downloaded departments"
        const val DOWNLOAD_DEPARTMENTS_FAILED ="Failed to download departments"
        const val DOWNLOAD_SECTIONS_DONE ="Downloaded sections"
        const val DOWNLOAD_SECTIONS_FAILED ="Failed to download sections"
       const val CASUAL_NAME = "casual"
        const val  THEME_KEY = "dark_mode_key"
    }
}