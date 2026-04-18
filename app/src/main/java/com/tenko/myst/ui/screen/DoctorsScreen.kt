package com.tenko.myst.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tenko.myst.R
import com.tenko.myst.data.model.Doctor
import com.tenko.myst.data.serializable.ContactResponse
import com.tenko.myst.data.serializable.ReminderResponse
import com.tenko.myst.data.view.DoctorViewModel
import com.tenko.myst.navigation.AppScreens
import com.tenko.myst.ui.components.AddContactButton
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.BottomNavigationBar
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.CardDark
import com.tenko.myst.ui.theme.CardGray
import com.tenko.myst.ui.theme.CardPurple
import com.tenko.myst.ui.theme.RaisinBlack
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

/*val doctorsList = mutableListOf<Doctor>(
    Doctor(
        id = 0,
        imageRes = R.drawable.doctor0,
        name = "Dr. Fillmore",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
    Doctor(
        id = 1,
        imageRes = R.drawable.doctor1,
        name = "Dr. Maria Lexa",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
    Doctor(
        id = 2,
        imageRes = R.drawable.doctor2,
        name = "Dr. Pullen",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
    Doctor(
        id = 3,
        imageRes = R.drawable.doctor3,
        name = "Dr. Rodrigo",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
    Doctor(
        id = 4,
        imageRes = R.drawable.doctor4,
        name = "Dr. Random",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
)*/

@Composable
fun DoctorsScreen(navController: NavHostController, viewModel: DoctorViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.fetchContacts()
        viewModel.fetchReminders()
    }

    var selectedDoctorName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Lista de Doctores",
                onBackClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = { AddContactButton(onClick = { navController.navigate(AppScreens.AddDoctorScreen.route) }) },
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
//                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 30.dp, bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(viewModel.contacts) { contact ->
                    DoctorCardTest(contact = contact) {
                        selectedDoctorName = "${contact.name} ${contact.last_name ?: ""}"
                        viewModel.filterRemindersByContact(contact.id_contact)
                    }
                }
                /*items(count = doctorsList.size) { index ->
                    val doctor = doctorsList[index]

                    DoctorCard(
                        imageRes = doctor.imageRes,
                        name = doctor.name,
                        subtitle = doctor.subtitle,
                        colors = when (index % 5) {
                            0 -> listOf(CardGray, RaisinBlack, Color.Gray)
                            1 -> listOf(CardPurple, White, AntiFlashWhite)
                            2 -> listOf(Tekhelet, White, AntiFlashWhite)
                            3 -> listOf(CardDark, White, AntiFlashWhite)
                            else -> listOf(RaisinBlack, White, AntiFlashWhite)
                        },
                        onClick = {
                            navController.navigate("doctor_details_screen/${doctor.id}")
                        }
                    )
                }*/
            }

            if (selectedDoctorName.isNotEmpty()) {
                Spacer(Modifier.height(224.dp))
                HorizontalDivider(Modifier.padding(top = 16.dp))
                Text("Citas con: $selectedDoctorName", style = MaterialTheme.typography.displayLarge, color = Color(0xFF8E44AD))

                if (viewModel.filteredReminders.isEmpty()) {
                    Text("No hay recordatorios vinculados a este contacto.", modifier = Modifier.padding(top = 8.dp))
                } else {
                    LazyColumn(/*modifier = Modifier.weight(0.8f)*/) {
                    items(viewModel.filteredReminders) { reminder ->
                        ReminderSmallItem(reminder)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorCardTest(contact: ContactResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Icono genérico de doctor
            Box(
                modifier = Modifier.size(50.dp).background(Color(0xFFF3E5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👨‍⚕️", fontSize = 24.sp)
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text("${contact.name} ${contact.last_name ?: ""}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                Text(contact.email ?: "Sin email", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ReminderSmallItem(reminder: ReminderResponse) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9F9F9)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(reminder.title ?: "Sin título", fontWeight = FontWeight.Bold)
            Text("${reminder.date} a las ${reminder.time}", style = MaterialTheme.typography.labelSmall)
        }
    }
}