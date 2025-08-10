package com.example.airdry.util

class Constants {
    companion object {
        const val LOCAL_DATABASE_NAME="AirDryDatabase"

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

    }
}