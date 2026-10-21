package rw.momo.directory.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = androidx.compose.ui.graphics.Color(0xFF1A1A1A),
                    navigationIconContentColor = androidx.compose.ui.graphics.Color(0xFF1A1A1A)
                )
            )
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VerificationBanner(service)

            if (needsParam) {
                OutlinedTextField(
                    value = billId, onValueChange = { billId = it },
                    label = { Text("Irembo Bill ID") },
                    placeholder = { Text("e.g. 1234567890") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(20.dp)
            ) {
                Column {
                    Text("USSD code", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(Modifier.height(6.dp))
                    Text(resolvedCode, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { Dialer.open(context, resolvedCode) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Call, null); Spacer(Modifier.width(6.dp)); Text("Dial")
                        }
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(resolvedCode))
                                Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.ContentCopy, null); Spacer(Modifier.width(6.dp)); Text("Copy")
                        }
                    }
                }
            }

            if (service.steps.isNotEmpty()) {
                Text("Steps", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    service.steps.forEachIndexed { i, step ->
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                Modifier.size(24.dp).clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) { Text("${i + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.width(10.dp))
                            Text(step, fontSize = 15.sp)
                        }
                    }
                }
            }

            if (service.source != null) {
                Text("Source", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(service.source, fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Security: never share your MoMo PIN. This app only prepares the dialer - it cannot perform a payment for you.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}


Write-File "$root\$pkg\MainActivity.kt" @'
package rw.momo.directory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import rw.momo.directory.data.Service
import rw.momo.directory.ui.*
import rw.momo.directory.ui.theme.MoMoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoMoTheme {
                val vm: MainViewModel = viewModel()
                val state by vm.state.collectAsState()
                var selected by remember { mutableStateOf<Service?>(null) }
                if (selected == null) {
                    HomeScreen(
                        state = state,
                        categories = vm.allCategories,
                        onQuery = vm::setQuery,
                        onCategory = vm::setCategory,
                        onOpen = { vm.pushRecent(it.id); selected = it },
                        onFavorite = vm::toggleFavorite
                    )
                } else {
                    ServiceDetailScreen(service = selected!!, onBack = { selected = null })
                }
            }
        }
    }
}
