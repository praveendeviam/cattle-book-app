package io.github.praveendeviam.cattlebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.praveendeviam.cattlebook.ui.navigation.AppNavigation
import io.github.praveendeviam.cattlebook.ui.theme.CattleBookTheme

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
