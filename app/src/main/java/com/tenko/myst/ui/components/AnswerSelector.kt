package com.tenko.myst.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tenko.myst.data.model.AnswerType
import com.tenko.myst.data.model.ClinicalQuestion
import com.tenko.myst.ui.screen.showDatePicker
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.White
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AnswerSelector(question: ClinicalQuestion, onAnswer: (String) -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    val context = LocalContext.current

    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = White,
        unfocusedContainerColor = AntiFlashWhite,
        focusedBorderColor = PompAndPower,
        unfocusedBorderColor = Color.Transparent,
        focusedTrailingIconColor = PompAndPower,
        unfocusedTrailingIconColor = SweetGrey,
        unfocusedPlaceholderColor = Color.Gray,
        disabledContainerColor = AntiFlashWhite,
        disabledBorderColor = Color.Transparent
    )

    when (val type = question.type) {
        is AnswerType.Binary -> {
            Row {
                Button(onClick = { onAnswer("Sí") }) { Text("Sí") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onAnswer("No") }) { Text("No") }
            }
        }
        is AnswerType.SingleChoice -> {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                type.options.forEach { (key, value) ->
                    OutlinedButton(onClick = { onAnswer(value) }) { Text(value) }
                }
            }
        }
        is AnswerType.Text -> {
            BottomBar(onAnswer)
        }
        is AnswerType.Numeric -> {
            Row {
                ChatInput(onSend = onAnswer, modifier = Modifier.imePadding(), isNumeric = true)
            }
        }
        is AnswerType.DatePicker -> {
            Column() {
                DatePickerField("Selecciona la fecha", birthDate?.format(formatter) ?: "", colors) {
                    showDatePicker(context) { date -> birthDate = date }
                }

                if(birthDate != null) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(
                        onClick = { onAnswer(birthDate!!.format(formatter)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PompAndPower,
                            contentColor = White
                        )
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
        is AnswerType.MultiChoice -> {
            // Estado para recordar qué llaves (keys) están seleccionadas
            val selectedOptions = remember { mutableStateListOf<String>() }
            val purpleColor = Color(0xFF8E44AD) // Morado

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                type.options.forEach { (key, value) ->
                    val isSelected = selectedOptions.contains(key)

                    OutlinedButton(
                        onClick = {
                            if (isSelected) selectedOptions.remove(key)
                            else selectedOptions.add(key)
                        },
                        modifier = Modifier.padding(vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        // Cambiamos el color según la selección
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) purpleColor else Color.Transparent,
                            contentColor = if (isSelected) Color.White else purpleColor
                        ),
                        border = BorderStroke(1.dp, purpleColor)
                    ) {
                        Text(value)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

            }
            // Botón para confirmar la selección múltiple
            Button(
                onClick = {
                    // Enviamos las opciones como un string separado por comas o JSON
                    if (selectedOptions.isNotEmpty()) {
                        onAnswer(selectedOptions.joinToString(", "))
                        selectedOptions.clear()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Confirmar selección", color = Color.White)
            }
        }
    }
}