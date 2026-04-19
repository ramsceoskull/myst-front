package com.tenko.myst.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tenko.myst.R
import com.tenko.myst.data.serializable.UserResponse
import com.tenko.myst.data.view.AuthViewModel
import com.tenko.myst.navigation.AppScreens
import com.tenko.myst.ui.components.ActionCard
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.BottomNavigationBar
import com.tenko.myst.ui.components.MenuItem
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.StarsLove
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    authViewModel.getUser(navController)
    val user = authViewModel.currentUser

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Perfil",
                onBackClick = { navController.popBackStack() },
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileSection(user)

            PlanAndInviteSection()

            MenuItem("Report History", R.drawable.file_pdf_solid_full)
            MenuItem("Clinical History", R.drawable.folder_open_solid_full)
            MenuItem("Help", R.drawable.circle_question_solid_full)
            MenuItem("Editar Perfil", R.drawable.gear_solid_full) {
                navController.navigate(AppScreens.UpdateProfileScreen.route)
            }
            MenuItem("Cerrar sesión", R.drawable.door_open_solid_full)
        }
    }
}

@Composable
fun ProfileSection(user: UserResponse?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = user?.picture,
            contentDescription = "Profile Photo",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(2.dp, AntiFlashWhite, CircleShape),
            placeholder = painterResource(R.drawable.profile_picture_placeholder),
            error = painterResource(R.drawable.profile_picture_placeholder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user?.name ?: "Nombre de Usuario",
            fontSize = 32.sp,
            color = Tekhelet,
            fontWeight = FontWeight.SemiBold,
            fontFamily = StarsLove
        )

        Text(
            text = user?.email ?: "Correo electrónico",
            color = SweetGrey,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PlanAndInviteSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AntiFlashWhite,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionCard(
            icon = Icons.Default.CardMembership,
            title = "Prueba",
            subtitle = "Tipo de plan",
            enabled = false
        )

//        ActionCard(
//            icon = Icons.Default.Favorite,
//            title = "Invita",
//            subtitle = "A amistades",
//            onClick = {  }
//        )
    }
}