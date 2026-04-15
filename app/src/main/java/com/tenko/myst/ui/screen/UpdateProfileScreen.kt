package com.tenko.myst.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.tenko.myst.data.api.TokenManager
import com.tenko.myst.data.view.AuthViewModel
import com.tenko.myst.data.view.ProfilePictureViewModel
import com.tenko.myst.launcher.rememberCameraLauncher
import com.tenko.myst.launcher.rememberGalleryLauncher
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.DeleteAccountRow
import com.tenko.myst.ui.components.InfoRow
import com.tenko.myst.ui.components.PhotoActionsSection
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(navController: NavController, viewModel: AuthViewModel, tokenManager: TokenManager, profileViewModel: ProfilePictureViewModel) {
    viewModel.getUser(navController)
    val user = viewModel.currentUser

    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val photoUri by profileViewModel.photoUri.collectAsState()

//    URI temporal para camara
    val tempUri = remember {
        val file = File.createTempFile("temp_photo", ".jpg", context.cacheDir)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

//    Launchers
    val galleryLauncher = rememberGalleryLauncher {
        profileViewModel.updatePhoto(it)
    }
    val cameraLauncher = rememberCameraLauncher(tempUri) { success ->
        if(success)
            profileViewModel.updatePhoto(tempUri)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Editar Perfil",
                onBackClick = { navController.popBackStack() }
            )
        },
        contentColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(White)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                PhotoActionsSection(
                    imageUrl = photoUri?.toString(),
                    onEditClick = { galleryLauncher.launch("image/*") },
                    onRemoveClick = { profileViewModel.removePhoto() }
                )

                Text(
                    text = "Tus datos personales",
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Tekhelet
                )

                InfoRow(
                    label = "Nombre",
                    value = user?.name ?: "Jane Doe",
                    onClick = { /* Lógica para editar nombre */ }
                )

                InfoRow(
                    label = "Correo electrónico",
                    value = user?.email ?: "tenko@myst.com",
                    showArrow = false,
                    onClick = { /* Lógica para editar correo */ }
                )

                Text(
                    text = "Otro",
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Tekhelet
                )

                DeleteAccountRow(
                    label = "Eliminar cuenta",
                    onClick = { showDialog = true }
                )

                /*if(showDialog)
                    Column {
                        Text("Esta acción no se puede deshacer.\nPor favor, ingresa tu contraseña para confirmar:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Contraseña") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                        Row {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                            TextButton(onClick = {
                                viewModel.deleteUser(password, tokenManager, navController)
//                                showDialog = false
                            }) {
                                Text("Eliminar definitivamente")
                            }
                        }
                    }*/
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("¿Estás segura?") },
                    text = {
                        Column {
                            Text("Esta acción no se puede deshacer.\nPor favor, ingresa tu contraseña para confirmar:")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Contraseña") },
                                visualTransformation = PasswordVisualTransformation()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            println("--- MYST DEBUG: confirmar ---")
                            if(password.isNotBlank()) {
                                println("--- MYST DEBUG: if---")
                                viewModel.deleteUser(password, tokenManager, navController)
                                println("--- MYST DEBUG: listo ---")
                                showDialog = false
                            }
                        }) {
                            Text("Eliminar definitivamente")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}