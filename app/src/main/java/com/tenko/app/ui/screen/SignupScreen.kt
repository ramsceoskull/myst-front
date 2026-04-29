package com.tenko.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.data.serializable.UserCreate
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.regex.isValidEmail
import com.tenko.app.regex.isValidPassword
import com.tenko.app.ui.components.AutoScrollingCarousel
import com.tenko.app.ui.components.LoginRedirectText
import com.tenko.app.ui.components.TermsAndPrivacyText
import com.tenko.app.ui.components.emailInput
import com.tenko.app.ui.components.nameInput
import com.tenko.app.ui.components.passwordInput
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun SignupScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var acceptedTerms by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    /*val accepted = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("termsAccepted")
//        ?.observeAsState()

    val isAccepted = accepted?.value == true*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
//            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, end = 25.dp, bottom = 15.dp, start = 25.dp)
        ) {
            Text(
                text = "Bienvenida\na Myst",
                color = Tekhelet,
                fontSize = 34.sp,
                fontFamily = StarsLove,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 45.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Regístrate",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Crea tu cuenta para empezar a usar Myst",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val (nameValue, initialsValue) = nameInput()
            val emailValue = emailInput()
            val passwordValue = passwordInput()
            val isFormValid = isValidEmail(emailValue) && isValidPassword(passwordValue)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val newUser = UserCreate(
                        name = nameValue,
                        email = emailValue,
                        password = passwordValue,
                        initials = if(initialsValue.length == 2) initialsValue else nameValue.take(2).uppercase(),
                        picture = null
                    )
                    viewModel.createUser(newUser, navController)
                },
                enabled = isFormValid/* && acceptedTerms*/,
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
                    text = "Continuar",
                    color = Color.White,
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginRedirectText(navController)
            TermsAndPrivacyText(navController)
        }
    }

    if (showTerms) {
        /*TermsDialog(
            pdfResId = R.raw.terms, // coloca tu PDF en res/raw
            onDismiss = { showTerms = false },
            onAccept = {
                acceptedTerms = true
                showTerms = false
            }
        )*/
        PdfViewerScreen(
//            pdfResId = R.raw.terms, // coloca tu PDF en res/raw
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            onAccept = {
                acceptedTerms = true
                showTerms = false
            },
            onDismiss = { showTerms = false }
        )
    }
}