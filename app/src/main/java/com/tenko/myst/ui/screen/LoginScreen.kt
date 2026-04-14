package com.tenko.myst.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tenko.myst.data.api.TokenManager
import com.tenko.myst.data.view.AuthViewModel
import com.tenko.myst.navigation.AppScreens
import com.tenko.myst.regex.isValidEmail
import com.tenko.myst.regex.isValidPassword
import com.tenko.myst.ui.components.AutoScrollingCarousel
import com.tenko.myst.ui.components.SignupRedirectText
import com.tenko.myst.ui.components.emailInput
import com.tenko.myst.ui.components.passwordInput
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.StarsLove
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.padding(top = 60.dp).padding(horizontal = 25.dp)) {
            Text(
                text = "Bienvenida de nuevo\na Myst",
                color = Tekhelet,
                fontSize = 34.sp,
                fontFamily = StarsLove,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 45.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Inicio de sesión",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            val email = emailInput(false)
            val password = passwordInput(false)
            val isFormValid = isValidEmail(email) && isValidPassword(password)

            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = { if(isValidEmail(email)) viewModel.forgotPassword(email, navController) }
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = PompAndPower,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(email, password, navController, tokenManager) },
                enabled = isFormValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Tekhelet,
                    disabledContainerColor = SweetGrey
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    color = White,
                    fontSize = 25.sp,
                    fontFamily = StarsLove,
                    fontWeight = FontWeight.ExtraLight,
                    modifier = Modifier.offset(y = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AutoScrollingCarousel()

        Spacer(modifier = Modifier.height(24.dp))

        SignupRedirectText(navController)
    }
}