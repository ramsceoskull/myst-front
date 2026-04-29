package com.tenko.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenko.app.R
import com.tenko.app.data.model.Genre
import com.tenko.app.data.model.Speciality
import com.tenko.app.data.serializable.ContactCreate
import com.tenko.app.data.serializable.ContactUpdate
import com.tenko.app.data.view.DoctorViewModel
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomBar
import com.tenko.app.ui.components.emailInput
import com.tenko.app.ui.components.nameInput
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.PompAndPower

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddDoctorScreen(
    viewModel: DoctorViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf(Genre.FEMALE) }
    var specialty by remember { mutableStateOf(Speciality.GYNECOLOGIST) }
    var phoneNumber by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    val avatarPreview = getRandomAvatar(genre)
    val aboutPreview = getAbout(specialty)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Agregar Doctor",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BottomBar(
                onClick = {
                    val newContact = ContactCreate(
                        name = name,
                        last_name = lastName,
                        email = email,
                        about = getAbout(specialty),
                        specialty = specialty.name,
                    )
                    viewModel.createContact(newContact)
                    viewModel.nextStep()
                },
                currentStep = viewModel.currentStep
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedContent(
                targetState = viewModel.currentStep,
                transitionSpec = {
                    slideInHorizontally { it }.togetherWith(slideOutHorizontally { -it })
                },
                label = "Slider page"
            ) { step ->
                when (step) {
                    0 -> {
                        Column(modifier = Modifier.padding(horizontal = 25.dp, vertical = 30.dp)) {
                            Text(
                                text = "Datos personales",
                                color = PompAndPower,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            name = nameInput(false, "Nombre").first
                            lastName = nameInput(false, "Apellido").first
                            email = emailInput(false)

                            Text("Género: $genre")
                            Text("Especialidad: $specialty")

                            Card {
                                Column(Modifier.padding(12.dp)) {

                                    Text("Vista previa", style = MaterialTheme.typography.titleMedium)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Avatar asignado:")
                                    Image(
                                        painter = painterResource(getRandomAvatar(genre)),
                                        contentDescription = "Avatar del médico",
                                        modifier = Modifier.size(100.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Acerca de mí:")
                                    Text(aboutPreview)
                                }
                            }

                            // Aquí puedes usar Dropdowns para genero y especialidad
                        }
                    }
                    1 -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(phoneNumber, { phoneNumber = it }, label = { Text("Teléfono") })
                            OutlinedTextField(street, { street = it }, label = { Text("Calle") })
                            OutlinedTextField(city, { city = it }, label = { Text("Ciudad") })

                            Row {
                                Button(onClick = { viewModel.previousStep() }) {
                                    Text("Atrás")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(onClick = {
                                    val updateInfo = ContactUpdate(
                                        phone_number = phoneNumber,
                                        address = "$street, $city"
                                    )
                                    viewModel.updateContact(
                                        idContact = viewModel.contacts.last().id_contact, // Asumiendo que el nuevo contacto es el último
                                        updateInfo,
                                        onBackClick
                                    )
//                viewModel.nextStep()
                                }) {
                                    Text("Finalizar")
                                }
                            }
                        }
                    }
//                2 -> StepResumen(viewModel)
                }
            }
        }
    }
}

fun getRandomAvatar(genre: Genre): Int {
    val femaleAvatar = listOf(
        R.drawable.doctor0,
        R.drawable.doctor1,
        R.drawable.doctor4
    )

    val maleAvatar = listOf(
        R.drawable.doctor2,
        R.drawable.doctor3,
    )

    return if (genre == Genre.FEMALE) femaleAvatar.random() else maleAvatar.random()
}

fun getAbout(speciality: Speciality): String {
    return when (speciality) {
        Speciality.CARDIOLOGIST ->
            "Especialista en el diagnóstico y tratamiento de enfermedades del corazón."
        Speciality.PEDIATRICIAN ->
            "Encargado del cuidado integral de niños y adolescentes."
        Speciality.DERMATOLOGIST ->
            "Especialista en enfermedades de la piel, cabello y uñas."
        Speciality.NEUROLOGIST ->
            "Experto en trastornos del sistema nervioso, incluyendo cerebro y médula espinal."
        Speciality.GYNECOLOGIST ->
            "Especialista en salud femenina, embarazo y parto."
        Speciality.ENDOCRINOLOGIST ->
            "Encargado del diagnóstico y tratamiento de trastornos hormonales y metabólicos."
        else -> TODO()
    }
}