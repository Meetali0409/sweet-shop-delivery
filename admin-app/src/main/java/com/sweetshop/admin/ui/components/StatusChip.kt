package com.sweetshop.admin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sweetshop.admin.ui.theme.StatusCancelled
import com.sweetshop.admin.ui.theme.StatusConfirmed
import com.sweetshop.admin.ui.theme.StatusDelivered
import com.sweetshop.admin.ui.theme.StatusOutForDelivery
import com.sweetshop.admin.ui.theme.StatusPlaced
import com.sweetshop.admin.ui.theme.StatusPreparing

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = getStatusColors(status)
    val displayText = status.replace("_", " ")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = displayText,
            color = backgroundColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun PaymentStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status.uppercase()) {
        "PAID" -> StatusDelivered
        "PENDING" -> StatusPreparing
        "FAILED" -> StatusCancelled
        "REFUNDED" -> StatusPlaced
        else -> Color.Gray
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = backgroundColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}

private fun getStatusColors(status: String): Pair<Color, Color> {
    return when (status.uppercase()) {
        "PLACED" -> StatusPlaced to Color.White
        "CONFIRMED" -> StatusConfirmed to Color.White
        "PREPARING" -> StatusPreparing to Color.White
        "OUT_FOR_DELIVERY" -> StatusOutForDelivery to Color.White
        "DELIVERED" -> StatusDelivered to Color.White
        "CANCELLED" -> StatusCancelled to Color.White
        else -> Color.Gray to Color.White
    }
}
