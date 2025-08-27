package com.example.slavgorodbus.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke // <--- ДОБАВЛЕН ИМПОРТ
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite // Используйте Icons.Filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder // Используйте Icons.Filled.FavoriteBorder или Outlined вариант
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // <--- ДОБАВЛЕН ИМПОРТ, если будете использовать конкретные цвета
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slavgorodbus.data.model.BusSchedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCard(
    schedule: BusSchedule,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    isNextUpcoming: Boolean = false, // <--- НОВЫЙ ОПЦИОНАЛЬНЫЙ ПАРАМЕТР
    @SuppressLint("ModifierParameter") // Этот SuppressLint лучше убрать, если modifier используется стандартно
    modifier: Modifier = Modifier // Стандартное объявление modifier
) {
    // Определяем цвета и рамку в зависимости от isNextUpcoming
    val cardContainerColor = if (isNextUpcoming) {
        MaterialTheme.colorScheme.tertiaryContainer // Цвет фона для ближайшего
    } else {
        MaterialTheme.colorScheme.surface // Ваш обычный цвет фона
    }

    val cardContentColorPrimary = if (isNextUpcoming) {
        MaterialTheme.colorScheme.onTertiaryContainer // Основной цвет контента для ближайшего
    } else {
        MaterialTheme.colorScheme.primary // Ваш обычный основной цвет контента
    }

    val cardContentVariantColor = if (isNextUpcoming) {
        MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f) // Второстепенный цвет для ближайшего
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant // Ваш обычный второстепенный цвет
    }

    val borderStroke = if (isNextUpcoming) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary) // Рамка для ближайшего
    } else {
        null // Без рамки для обычных
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor // <--- Применяем вычисленный цвет фона
        ),
        border = borderStroke // <--- Применяем рамку
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DepartureInfo(
                modifier = Modifier.weight(1f),
                departureTime = schedule.departureTime,
                departureStopName = schedule.stopName,
                // Передаем цвета для DepartureInfo
                primaryTextColor = cardContentColorPrimary,
                variantTextColor = cardContentVariantColor,
                isUpcoming = isNextUpcoming // Можно передать для изменения FontWeight, если нужно
            )

            // Для ArrivalInfo можно сделать аналогично, если нужно менять цвета/стили
            // Пока оставляем как есть, но вы можете расширить
            "Яровое (МСЧ-128)".ArrivalInfo(
                modifier = Modifier.weight(1f),
                arrivalTime = schedule.arrivalTime,
                primaryTextColor = cardContentColorPrimary, // Пример передачи цвета
                variantTextColor = cardContentVariantColor  // Пример передачи цвета
            )

            if (onFavoriteClick != null) {
                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = onFavoriteClick,
                    // Можно изменить цвет иконки избранного для ближайшего рейса
                    iconTint = if (isNextUpcoming && isFavorite) MaterialTheme.colorScheme.tertiary
                    else if (isNextUpcoming) MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    else if (isFavorite) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun DepartureInfo(
    modifier: Modifier = Modifier,
    departureTime: String,
    departureStopName: String,
    primaryTextColor: Color, // <--- Новый параметр цвета
    variantTextColor: Color, // <--- Новый параметр цвета
    isUpcoming: Boolean // <--- Новый параметр для возможного изменения стиля
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = departureTime,
            style = MaterialTheme.typography.titleMedium.copy(
                // Можно сделать шрифт жирнее для ближайшего рейса
                fontWeight = if (isUpcoming) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = primaryTextColor // <--- Используем переданный цвет
        )
        Text(
            text = departureStopName,
            style = MaterialTheme.typography.bodySmall,
            color = variantTextColor // <--- Используем переданный цвет
        )
    }
}

@Composable
private fun String.ArrivalInfo(
    modifier: Modifier = Modifier,
    arrivalTime: String,
    primaryTextColor: Color, // <--- Новый параметр цвета (пример)
    variantTextColor: Color  // <--- Новый параметр цвета (пример)
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Column(
            horizontalAlignment = Alignment.Start // Внутренний Column выравнивается по Start, это может быть сделано намеренно
        ) {
            Text(
                text = arrivalTime,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold, // Оставляем Bold, или можно тоже сделать зависимым от isUpcoming
                    fontSize = 18.sp
                ),
                color = primaryTextColor // <--- Используем переданный цвет
            )
            Text(
                text = this@ArrivalInfo,
                style = MaterialTheme.typography.bodySmall,
                color = variantTextColor // <--- Используем переданный цвет
            )
        }
    }
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    iconTint: Color, // <--- Новый параметр для цвета иконки
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(start = 8.dp)
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
            tint = iconTint // <--- Используем переданный цвет
        )
    }
}