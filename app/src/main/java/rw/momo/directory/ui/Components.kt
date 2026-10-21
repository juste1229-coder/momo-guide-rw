package rw.momo.directory.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rw.momo.directory.data.CodeType
import rw.momo.directory.data.Service
import rw.momo.directory.data.VerificationStatus

@Composable
fun ServiceCard(service: Service, isFavorite: Boolean, onClick: () -> Unit, onFavorite: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Call, contentDescription = null) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(service.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CodeChip(service)
                    Spacer(Modifier.width(8.dp))
                    Text(service.category, fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun CodeChip(service: Service) {
    val (label, color) = when (service.codeType) {
        CodeType.DIRECT -> "Direct" to Color(0xFF2E7D32)
        CodeType.DIRECT_WITH_PARAMETER -> "Direct (ID)" to Color(0xFF1565C0)
        CodeType.MAIN_MENU -> "Menu" to Color(0xFF6D4C41)
    }
    Box(Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.15f))
        .padding(horizontal = 8.dp, vertical = 2.dp)
    ) { Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium) }
}

@Composable
fun VerificationBanner(service: Service) {
    val (text, bg) = when (service.verification) {
        VerificationStatus.OFFICIALLY_DOCUMENTED ->
            "Verified from official source" to Color(0xFFE8F5E9)
        VerificationStatus.NEEDS_CONFIRMATION ->
            "Menu path may change - confirm on your phone" to Color(0xFFFFF8E1)
    }
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bg).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (service.verification == VerificationStatus.OFFICIALLY_DOCUMENTED)
                    Icons.Filled.Verified else Icons.Filled.Info,
                contentDescription = null, tint = Color(0xFF444444)
            )
            Spacer(Modifier.width(8.dp))
            Text(text, fontSize = 13.sp, color = Color(0xFF333333))
        }
    }
}
