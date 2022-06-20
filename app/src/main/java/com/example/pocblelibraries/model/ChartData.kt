package com.example.pocblelibraries.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChartData(
    val entries: List<ChartPoint>
) : Parcelable


@Parcelize
data class ChartPoint(
    val x: Float,
    val y: Float
): Parcelable