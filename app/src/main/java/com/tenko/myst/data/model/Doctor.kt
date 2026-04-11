package com.tenko.myst.data.model

import com.tenko.myst.R

data class Doctor(
    val id: Int,
    val imageRes: Int = R.drawable.doctor0,
    val name: String = "Dr. John Doe",
    val subtitle: String = "Cardiologist",
    val about: String = "Dr. John Doe is a highly experienced cardiologist with over 20 years of practice. He specializes in treating heart conditions and has a passion for helping patients improve their cardiovascular health.",
    val email: String = "ramsesrame21@gmail.com",
    val phoneNumber: Number = 3333943613
)