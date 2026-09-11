package com.sweetshop.customer.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sweetshop.customer.ui.auth.AuthViewModel
import com.sweetshop.customer.ui.theme.SweetCream
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(1500)
        if (authViewModel.isLoggedIn()) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cake,
                contentDescription = "Sweet Shop",
                modifier = Modifier.size(100.dp),
                tint = SweetCream
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sweet Shop",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = SweetCream
            )

            Text(
                text = "Fresh & Delicious",
                fontSize = 16.sp,
                color = SweetCream.copy(alpha = 0.8f),
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = SweetCream,
                strokeWidth = 2.dp
            )
        }
    }
}
