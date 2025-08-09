package com.example.airdry.ui.screens.settings

import androidx.compose.runtime.Composable

import androidx.compose.material3.Text
@Composable
fun AboutScreen() {


    Text("About Screen")
    /**
     * THIS MUST A PAGE WITH two purposes  1. to explain purpose of app ,
     * 2. provide my contact details
     *  I want to use proper scrollable text UI , it must read like a scroll
     *  details : purpose of app is to allow the accurate and timely recording
     *   of workdone in AirDry production department  , it allows for  management of
     *   personell info , and recording of stacks done by workers at the end of each working day
     *    the settings provide for Stack Management so  you can set Stack data i.e name , number of pieces
     *      the application uses provided stack data to generate reports (Excel workbook )
     *      with 3 sheets 1  sheet with raw data all stacks by all personel in a given period
     *              sheet 2  with Daily summarised data how many cubes done by each person per day + total stacks
     *              sheet 3 with  summarised data for overall period  , total  cubes ,total stacks
     *              per person  over a period
     *
     *  My contact details (+26876911464, +268 79487461)  make text  actionable through telephony intent
     *  -- Let User know iam available for clarifications and help using this software  and  then   put
     *  a user licence  that does  prohibits the use of the application without my permission or knowledge
     * */
}