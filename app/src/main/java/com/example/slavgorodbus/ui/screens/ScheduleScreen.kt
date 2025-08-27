package com.example.slavgorodbus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.slavgorodbus.data.model.BusRoute
import com.example.slavgorodbus.data.model.BusSchedule
import com.example.slavgorodbus.ui.components.ScheduleCard
import com.example.slavgorodbus.ui.viewmodel.BusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    route: BusRoute?,
    onBackClick: () -> Unit,
    viewModel: BusViewModel? = null
) {
    val schedules = remember(route) {
        if (route != null) {
            generateSampleSchedules(route.id)
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = route?.name ?: "Расписание",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (route == null) {
            NoRouteSelectedMessage(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        } else {
            ScheduleDetails(
                modifier = Modifier.padding(paddingValues),
                route = route,
                schedules = schedules,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun NoRouteSelectedMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Маршрут не выбран",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Пожалуйста, выберите маршрут для просмотра расписания и деталей.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScheduleDetails(
    modifier: Modifier = Modifier,
    route: BusRoute,
    schedules: List<BusSchedule>,
    viewModel: BusViewModel?
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 16.dp)
    ) {
        item {
            RouteDetailsSummaryCard(route = route)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Время отправления:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(
            items = schedules,
            key = { schedule -> schedule.id }
        ) { schedule ->
            ScheduleCard(
                schedule = schedule,
                isFavorite = viewModel?.isFavoriteTime(schedule.id) ?: false,
                onFavoriteClick = {
                    viewModel?.let { vm ->
                        if (vm.isFavoriteTime(schedule.id)) {
                            vm.removeFavoriteTime(schedule.id)
                        } else {
                            vm.addFavoriteTime(schedule)
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun RouteDetailsSummaryCard(route: BusRoute) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            route.travelTime?.let {
                DetailRow(label = "Время в пути:", value = it)
            }
            route.pricePrimary?.let {
                DetailRow(label = "Стоимость:", value = it)
            }
            route.paymentMethods?.let {
                DetailRow(
                    label = "Способы оплаты:",
                    value = it,
                    allowMultiLineValue = false
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    allowMultiLineValue: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (allowMultiLineValue) Int.MAX_VALUE else 1,
            overflow = if (allowMultiLineValue) TextOverflow.Clip else TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun generateSampleSchedules(routeId: String): List<BusSchedule> {
    return when (routeId) {
        "102" -> listOf(
            BusSchedule("102_1", "102", "Славгород (Рынок)", "06:25", "07:00", 1),
            BusSchedule("102_2", "102", "Славгород (Рынок)", "06:45", "07:20", 1),
            BusSchedule("102_3", "102", "Славгород (Рынок)", "07:00", "07:35", 1),
            BusSchedule("102_4", "102", "Славгород (Рынок)", "07:20", "07:55", 1),
            BusSchedule("102_5", "102", "Славгород (Рынок)", "07:40", "08:20", 1),
            BusSchedule("102_6", "102", "Славгород (Рынок)", "08:00", "08:40", 1),
            BusSchedule("102_7", "102", "Славгород (Рынок)", "08:25", "09:00", 1),
            BusSchedule("102_8", "102", "Славгород (Рынок)", "08:40", "09:20", 1),
            BusSchedule("102_9", "102", "Славгород (Рынок)", "09:00", "09:40", 1),
            BusSchedule("102_10", "102", "Славгород (Рынок)", "09:20", "10:00", 1),
            BusSchedule("102_11", "102", "Славгород (Рынок)", "09:35", "10:15", 1),
            BusSchedule("102_12", "102", "Славгород (Рынок)", "10:00", "10:35", 1),
            BusSchedule("102_13", "102", "Славгород (Рынок)", "10:25", "11:10", 1),
            BusSchedule("102_14", "102", "Славгород (Рынок)", "10:50", "11:30", 1),
            BusSchedule("102_15", "102", "Славгород (Рынок)", "11:10", "11:55", 1),
            BusSchedule("102_16", "102", "Славгород (Рынок)", "11:35", "12:20", 1),
            BusSchedule("102_17", "102", "Славгород (Рынок)", "12:05", "12:40", 1),
            BusSchedule("102_18", "102", "Славгород (Рынок)", "12:30", "13:05", 1),
            BusSchedule("102_19", "102", "Славгород (Рынок)", "12:55", "13:30", 1),
            BusSchedule("102_20", "102", "Славгород (Рынок)", "13:15", "13:55", 1),
            BusSchedule("102_21", "102", "Славгород (Рынок)", "13:35", "14:15", 1),
            BusSchedule("102_22", "102", "Славгород (Рынок)", "14:05", "14:45", 1),
            BusSchedule("102_23", "102", "Славгород (Рынок)", "14:30", "15:10", 1),
            BusSchedule("102_24", "102", "Славгород (Рынок)", "14:55", "15:30", 1),
            BusSchedule("102_25", "102", "Славгород (Рынок)", "15:20", "15:55", 1),
            BusSchedule("102_26", "102", "Славгород (Рынок)", "15:45", "16:20", 1),
            BusSchedule("102_27", "102", "Славгород (Рынок)", "16:10", "16:45", 1),
            BusSchedule("102_28", "102", "Славгород (Рынок)", "16:35", "17:10", 1),
            BusSchedule("102_29", "102", "Славгород (Рынок)", "17:05", "17:40", 1),
            BusSchedule("102_30", "102", "Славгород (Рынок)", "17:25", "18:10", 1),
            BusSchedule("102_31", "102", "Славгород (Рынок)", "17:50", "18:35", 1),
            BusSchedule("102_32", "102", "Славгород (Рынок)", "18:20", "19:00", 1),
            BusSchedule("102_33", "102", "Славгород (Рынок)", "18:50", "19:25", 1),
            BusSchedule("102_34", "102", "Славгород (Рынок)", "19:20", "20:00", 1),
            BusSchedule("102_35", "102", "Славгород (Рынок)", "20:00", "20:30", 1),
            BusSchedule("102_36", "102", "Славгород (Рынок)", "20:30", "21:00", 1)
        )
        else -> emptyList()
    }
}