package com.example.airdry.domain.repository.implementations

import android.app.Application
import android.util.Log
import com.example.airdry.domain.repository.interfaces.ReportConfigRepository
import javax.inject.Inject
import java.io.File
import android.os.Environment
import com.example.airdry.R

class ReportConfigRepositoryImpl @Inject constructor(val app: Application) : ReportConfigRepository
{

    /***************************************************************************
     * setUpExternalMemoryFileDirectories() -> creates in External Storage
     *                                  the directory  "AirDry"
     *                                  subdirectories : PDF
     *                                                 : Excel
     ***************************************************************************/
    override fun setUpExternalDirectories() {
        val hostPath = Environment.getExternalStorageDirectory().path
        val rootDirectory = File(hostPath + "/" + app.getString(R.string.app_name))

        if (!rootDirectory.exists())
            rootDirectory.mkdir()
        val pdfDirectory =
            File(hostPath + "/" + app.getString(R.string.app_name) + "/" + "PDF")
        val csvDirectory =
            File(hostPath + "/" + app.getString(R.string.app_name) + "/" + "Excel")

        if (!pdfDirectory.exists())
            pdfDirectory.mkdir()
        if (!csvDirectory.exists())
            csvDirectory.mkdir()
    }
    /***************************************************************************
     * setUpExternalMemoryFileDirectories() -> creates in External Storage
     *                                  the directory  "AirDry "
     *                                  subdirectories : PDF
     *                                                 : Excel
     ***************************************************************************/
    override fun setUpInternalDirectories() {
        val hostPath = app.filesDir.absolutePath
        val rootDirectory = File("$hostPath/files")

        if (!rootDirectory.exists())
            rootDirectory.mkdir()



    }
}
