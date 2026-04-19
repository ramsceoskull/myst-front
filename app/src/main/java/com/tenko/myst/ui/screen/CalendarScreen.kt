package com.tenko.myst.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.tenko.myst.data.model.CalendarEvent
import com.tenko.myst.data.serializable.DailyLogCreate
import com.tenko.myst.data.view.CycleViewModel
import com.tenko.myst.ui.components.AddCalendarEvent
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.BottomNavigationBar
import com.tenko.myst.ui.components.DayBottomSheet
import com.tenko.myst.ui.theme.White
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavHostController, viewModel: CycleViewModel = viewModel()) {
    val currentMonth = remember { YearMonth.now() }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showSheet by remember { mutableStateOf(false) }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) { viewModel.fetchData() }

    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )
    Scaffold(
        topBar = { AppTopBar(
            title = "Calendario",
            onBackClick = { navController.popBackStack() }
        ) },
        floatingActionButton = {
            // Botón para abrir el registro de síntomas
            AddCalendarEvent({ showSheet = true })
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CalendarHeader(state)

            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    val date = day.date

                    // 1. Determinar el color del día
                    val dayColor = when {
                        // Si el día está dentro de un ciclo registrado (Pasado)
                        viewModel.cycles.any { cycle ->
                            val end = cycle.end_date ?: LocalDate.now()
                            !date.isBefore(cycle.start_date) && !date.isAfter(end)
                        } -> Color(0xFFFF6FAE) // Rosa/Púrpura

                        // Si el día está en el rango de predicción (Futuro)
                        viewModel.prediction?.let { pred ->
                            val start = LocalDate.parse(pred.predicted_cycle_range.start)
                            val end = LocalDate.parse(pred.predicted_cycle_range.end)
                            !date.isBefore(start) && !date.isAfter(end)
                        } ?: false -> Color.Red // Rojo para predicción

                        else -> Color.Transparent
                    }

                    DayCell(
                        day = date,
                        selected = date == selectedDate,
                        hasEvent = viewModel.dailyLogs.any { it.date == date },
                        statusColor = dayColor, // Pasamos el color calculado
                        onClick = {
                            selectedDate = date
                            showSheet = true
                        }
                    )
                }
            )
        }

        if (showSheet) {
            DailyLogFormSheet(
                date = selectedDate,
                onDismiss = { showSheet = false },
                onSave = { logData ->
                    viewModel.createDailyLog(logData) { showSheet = false }
                }
            )
        }
    }
}

@Composable
fun DayCell(
    day: LocalDate,
    selected: Boolean,
    hasEvent: Boolean,
    statusColor: Color = Color.Transparent, // Nueva propiedad
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    when {
                        selected -> Color(0xFF7B61FF) // Color de selección (Morado)
                        statusColor != Color.Transparent -> statusColor.copy(alpha = 0.2f) // Fondo suave para el rango
                        else -> Color.Transparent
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                day.dayOfMonth.toString(),
                color = when {
                    selected -> Color.White
                    statusColor != Color.Transparent -> statusColor // Texto del color del estado (ej. Rojo)
                    else -> Color.Black
                },
                fontWeight = if (statusColor != Color.Transparent) FontWeight.Bold else FontWeight.Normal
            )
        }

        // El puntito indicador de que hay un log diario (síntomas)
        if (hasEvent) {
            Box(
                Modifier
                    .padding(top = 2.dp)
                    .size(6.dp)
                    .background(
                        Color(0xFFFF6FAE), // Color para logs/eventos
                        CircleShape
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLogFormSheet(
    date: LocalDate,
    onDismiss: () -> Unit,
    onSave: (DailyLogCreate) -> Unit
) {
    // Estados para los campos que mencionaste
    var flow by remember { mutableStateOf<Int?>(null) } // 0-4 según tu FlowEnum
    var notes by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedHobbies by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Registro: ${date}", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // --- SECCIÓN: FLUJO MENSTRUAL (INT) ---
            Text("Flujo Menstrual", fontWeight = FontWeight.SemiBold)
            val flowOptions = listOf("Nulo", "Ligero", "Medio", "Abundante", "Goteo")
            flowOptions.forEachIndexed { index, label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { flow = index }
                ) {
                    RadioButton(selected = flow == index, onClick = { flow = index })
                    Text(label)
                }
            }

            // --- SECCIÓN: PESO (FLOAT) ---
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- SECCIÓN: HOBBIES (STRING) ---
            // Aquí puedes usar un TextField o chips que concatenen el string
            Text("Hobbies y Actividades", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = selectedHobbies,
                onValueChange = { selectedHobbies = it },
                placeholder = { Text("Ej: Leer, Bailar...") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- SECCIÓN: NOTAS ---
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas adicionales") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    onSave(
                        DailyLogCreate(
                            date = date,
                            menstrual_flow = flow,
                            weight = weight.toFloatOrNull(),
                            hobbies_activities = selectedHobbies.ifBlank { null },
                            notes = notes.ifBlank { null }
                            // Agrega aquí el resto de campos de tu data class
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text("Guardar Registro")
            }
        }
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CalendarScreen(navController: NavHostController) {
//    val currentMonth = remember { YearMonth.now() }
//
//    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
//    var showSheet by remember { mutableStateOf(false) }
//    val events = remember { mutableStateListOf<CalendarEvent>() }
//
//    val state = rememberCalendarState(
//        startMonth = currentMonth.minusMonths(12),
//        endMonth = currentMonth.plusMonths(12),
//        firstVisibleMonth = currentMonth,
//        firstDayOfWeek = DayOfWeek.MONDAY
//    )
//
//    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
//
//    Scaffold(
//        modifier = Modifier
//            .nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            AppTopBar(
//                title = "Calendario",
//                onBackClick = { navController.popBackStack() }
//            )
//        },
//        bottomBar = {
//            BottomNavigationBar(navController)
//        },
//        floatingActionButton = { AddCalendarEvent({}) },
//        floatingActionButtonPosition = FabPosition.End,
//        containerColor = White,
//    ) { padding ->
//        Box (modifier = Modifier.padding(padding)) {
//            Column (
//                modifier = Modifier
//                    .fillMaxWidth().scrollable(state, Orientation.Vertical),
//            ) {
//                CalendarHeader(state)
//
//                HorizontalCalendar(
//                    state = state,
//                    dayContent = { day ->
//                        DayCell(
//                            day = day.date,
//                            selected = day.date == selectedDate,
//                            hasEvent = events.any { it.date == day.date }
//                        ) {
//                            selectedDate = day.date
//                            showSheet = true
//                        }
//
//                    }
//                )
//
//            }
//
//            if (showSheet) {
//                DayBottomSheet(
//                    date = selectedDate,
//                    onDismiss = { showSheet = false },
//                    onSave = { note, symptoms ->
//                        events.removeAll { it.date == selectedDate }
//
//                        events.add(
//                            CalendarEvent(
//                                date = selectedDate,
//                                note = note,
//                                symptoms = symptoms
//                            )
//                        )
//
//                        showSheet = false
//                    }
//                )
//            }
//        }
//    }
//}

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavHostController) {
    val currentMonth = remember { YearMonth.now() }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showSheet by remember { mutableStateOf(false) }
    val events = remember { mutableStateListOf<CycleEvent>() }

    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(24),
        endMonth = currentMonth.plusMonths(24),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(
                title = "Calendario",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = PompAndPower
            ) {
                Icon(
                    painter = painterResource(R.drawable.plus_solid_full),
                    contentDescription = "Registrar síntoma",
                    Modifier.size(24.dp),
                    tint = White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = White,
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column {
                CalendarHeader(state)

                HorizontalCalendar(
                    state = state,
                    dayContent = { day ->

                        val dayEvents = events.filter {
                            it.date == day.date
                        }

                        DayCellPro(
                            day = day.date,
                            selected = day.date == selectedDate,
                            events = dayEvents
                        ) {
                            selectedDate = day.date
                            showSheet = true
                        }
                    }
                )

            }

            if (showSheet) {

                DayEditorSheet(
                    date = selectedDate,
                    onDismiss = { showSheet = false },
                    onSave = { newEvent ->

                        events.removeAll {
                            it.date == newEvent.date &&
                                    it.type == newEvent.type
                        }

                        events.add(newEvent)

                        showSheet = false
                    }
                )
            }
        }
    }
}*/

@Composable
fun CalendarHeader(state: CalendarState) {
    val month = state.firstVisibleMonth.yearMonth

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            month.month.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            month.year.toString()
        )

    }
}

@Composable
fun DayCell(
    day: LocalDate,
    selected: Boolean,
    hasEvent: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    when {
                        selected -> Color(0xFF7B61FF)
                        else -> Color.Transparent
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                day.dayOfMonth.toString(),
                color = if (selected) Color.White else Color.Black
            )

        }

        if (hasEvent) {
            Box(
                Modifier
                    .size(6.dp)
                    .background(
                        Color(0xFFFF6FAE),
                        CircleShape
                    )
            )
        }

    }
}