package com.tenko.myst.data.serializable
import kotlinx.serialization.Serializable

@Serializable
data class PredictionRange(val start: String,
                           val end: String)
