package rw.momo.directory.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import rw.momo.directory.data.CodeType
import rw.momo.directory.data.SavedNumbers
import rw.momo.directory.data.Service
import rw.momo.directory.data.ServiceRepository
import rw.momo.directory.util.Dialer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(service: Service, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val savedPrefs = remember { SavedNumbers(context) }
    var savedList by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    val paramsState = remember { mutableStateMapOf<String, String>() }
    val needsParam = service.codeType == CodeType.DIRECT_WITH_PARAMETER
    var showPinDialog by remember { mutableStateOf(false) }

    LaunchedEffect(service.id) {
        savedPrefs.getSaved(service.id).collect { savedList = it }
    }

    val resolvedCode = if (needsParam) {
        var code = service.entryCode
        service.parameters.forEach { param ->
            val value = paramsState[param]?.replace(",", "") ?: ""
            code = code.replace("{$param}", value)
        }
        code
    } else service.entryCode

    val isFormValid = !needsParam || service.parameters.all { !paramsState[it].isNullOrBlank() }

    if (showPinDialog) {
        var pin by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Enter MoMo PIN", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Review your transaction:")
                    Spacer(Modifier.height(8.dp))
                    service.parameters.forEach { param ->
                        Text("${param.replaceFirstChar { it.uppercase() }}: ${paramsState[param]}", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 5) pin = it },
                        label = { Text("5-digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = { 
                    showPinDialog = false
                    Dialer.open(context, resolvedCode) 
                }) { Text("Confirm & Dial") }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(service.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color(0xFF1A1A1A),
                    navigationIconContentColor = Color(0xFF1A1A1A)
                )
            )
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (needsParam) {
                Text("Transaction Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                service.parameters.forEach { param ->
                    if (param == "amount") {
                        OutlinedTextField(
                            value = paramsState[param] ?: "",
                            onValueChange = { paramsState[param] = ServiceRepository.formatAmount(it) },
                            label = { Text("Amount (RWF)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Text("FRw ", fontWeight = FontWeight.Bold) }
                        )
                    } else {
                        // Phone, meter, account, merchant
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = paramsState[param] ?: "",
                                onValueChange = { paramsState[param] = it },
                                label = { Text(param.replaceFirstChar { it.uppercase() }) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            IconButton(onClick = {
                                val valToSave = paramsState[param]
                                if (!valToSave.isNullOrBlank()) {
                                    scope.launch { savedPrefs.saveNumber(service.id, valToSave) }
                                    Toast.makeText(context, "Saved to favorites!", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Filled.BookmarkAdd, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                if (savedList.isNotEmpty()) {
                    Text("Favorites", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        savedList.forEach { saved ->
                            AssistChip(
                                onClick = { paramsState["phone"] = saved; paramsState["meter"] = saved; paramsState["account"] = saved },
                                label = { Text(saved) },
                                leadingIcon = { Icon(Icons.Filled.Person, null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // The Giant Yellow Button
            Button(
                onClick = { if (isFormValid) showPinDialog = true },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("SEND", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if (service.steps.isNotEmpty() && !needsParam) {
                Text("Steps", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    service.steps.forEachIndexed { i, step ->
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                Modifier.size(24.dp).clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) { Text("${i + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.width(10.dp))
                            Text(step, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

