package com.example.slavgorodbus.data.model

data class FavoriteTime(
    val id: String,
    val routeId: String,
    val routeNumber: String,
    val routeName: String,
    val stopName: String,
    val departureTime: String,
    val dayOfWeek: Int,
    val departurePoint: String,
    val isActive: Boolean = true
)