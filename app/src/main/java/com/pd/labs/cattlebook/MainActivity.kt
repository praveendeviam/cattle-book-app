package com.pd.labs.cattlebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pd.labs.cattlebook.ui.navigation.AppNavigation
import com.pd.labs.cattlebook.ui.theme.CattleBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CattleBookTheme {
                AppNavigation()
            }
        }
    }
}
