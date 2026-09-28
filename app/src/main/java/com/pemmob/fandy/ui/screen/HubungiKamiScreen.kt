package com.pemmob.fandy.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pemmob.fandy.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubungiKamiScreen(navController: NavController?) {
    // 1. Deklarasi Variabel (Stateful)
    var emailText by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }
    var problemType by rememberSaveable { mutableStateOf("Pilih Tipe Pesan") }
    var isAgreed by rememberSaveable { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 2. Logika Validasi
    val isEmailValid = emailText.contains("@") && emailText.isNotBlank()
    val isMessageValid = messageText.length >= 10
    val isFormValid = isEmailValid && isMessageValid && isAgreed && problemType != "Pilih Tipe Pesan"

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 3. Scaffold & TopAppBar (Mempertahankan kode lama Anda)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hubungi Kami") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_icon),
                            contentDescription = "Back Icon"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        // 4. Memanggil Stateless UI
        StatelessFormHubungiKami(
            modifier = Modifier.padding(paddingValues),
            email = emailText,
            onEmailChange = { emailText = it },
            isEmailValid = isEmailValid,
            message = messageText,
            onMessageChange = { messageText = it },
            isMessageValid = isMessageValid,
            problemType = problemType,
            onProblemTypeChange = { problemType = it },
            isAgreed = isAgreed,
            onAgreedChange = { isAgreed = it },
            imageUri = imageUri,
            onImagePicked = { imageUri = it },
            isFormValid = isFormValid,
            onSubmit = {
                scope.launch {
                    snackbarHostState.showSnackbar(message = "Pesan Terkirim!")
                }
            }
        )
    }
}

// ==========================================
// KODE UI (STATELESS)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatelessFormHubungiKami(
    modifier: Modifier = Modifier,
    email: String, onEmailChange: (String) -> Unit, isEmailValid: Boolean,
    message: String, onMessageChange: (String) -> Unit, isMessageValid: Boolean,
    problemType: String, onProblemTypeChange: (String) -> Unit,
    isAgreed: Boolean, onAgreedChange: (Boolean) -> Unit,
    imageUri: Uri?, onImagePicked: (Uri?) -> Unit,
    isFormValid: Boolean, onSubmit: () -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onImagePicked(uri) }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hubungi Kami",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Anda") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
            },
            isError = email.isNotEmpty() && !isEmailValid,
            supportingText = { if (email.isNotEmpty() && !isEmailValid) Text("Format Email Salah") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }
        val options = listOf("Pertanyaan", "Keluhan", "Saran")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = problemType,
                onValueChange = { },
                label = { Text("Tipe Pesan") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectionOption) },
                        onClick = {
                            onProblemTypeChange(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            label = { Text("Pesan") },
            isError = message.isNotEmpty() && !isMessageValid,
            supportingText = { Text("Pesan minimal 10 karakter") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Unggah Bukti (Screenshot / Foto)")
        }

        if (imageUri != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.padding(all = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "File")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("File terpilih: ${imageUri.lastPathSegment}")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAgreed, onCheckedChange = onAgreedChange)
            Text("Saya menyetujui syarat & ketentuan")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Kirim Pesan", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}