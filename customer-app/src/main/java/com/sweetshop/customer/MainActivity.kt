package com.sweetshop.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sweetshop.customer.navigation.SweetShopNavGraph
import com.sweetshop.customer.ui.theme.SweetShopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SweetShopTheme {
                SweetShopNavGraph()
            }
        }
    }
}
