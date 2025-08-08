package com.example.airdry.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.theme.ForestGreen
import com.example.airdry.ui.theme.Typography

@Composable
    fun Settings(
        navController: NavController
    ){
        Column {
            //Header row

             Text("Main Settings",
                 modifier = Modifier.padding(16.dp).align(
                     Alignment.CenterHorizontally
                 )
                     ,
                 style = Typography.titleLarge

             )
            GreenDivider()
            Text("Check For Updates"
            , modifier = Modifier.padding(16.dp).align(
                    Alignment.Start
                ).clickable{
                    navController.navigate(route = Screen.ManageUpdates.route)
                }
                ,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface

            )

         GreenDivider()
            Text("Manage Stack Settings",
                    modifier = Modifier.padding(16.dp).align(Alignment.Start).clickable{
                  navController.navigate(route = Screen.ManageStacks.route)
            },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface

            )
            GreenDivider()
        }


    }

@Composable
fun GreenDivider(){
    Divider(
        color = ForestGreen,
        thickness = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )

}