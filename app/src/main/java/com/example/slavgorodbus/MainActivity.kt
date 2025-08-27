package com.example.slavgorodbus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.slavgorodbus.ui.navigation.BottomNavigation
import com.example.slavgorodbus.ui.navigation.Screen
import com.example.slavgorodbus.ui.screens.AboutScreen
import com.example.slavgorodbus.ui.screens.FavoriteTimesScreen
import com.example.slavgorodbus.ui.screens.HomeScreen
import com.example.slavgorodbus.ui.screens.RouteDetailsScreen
import com.example.slavgorodbus.ui.screens.ScheduleScreen
import com.example.slavgorodbus.ui.screens.SettingsScreen
import com.example.slavgorodbus.ui.theme.SlavgorodBusTheme
import com.example.slavgorodbus.ui.viewmodel.AppTheme
import com.example.slavgorodbus.ui.viewmodel.BusViewModel
import com.example.slavgorodbus.ui.viewmodel.ThemeViewModel
import com.example.slavgorodbus.ui.viewmodel.ThemeViewModelFactory

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels {
        ThemeViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentAppTheme by themeViewModel.currentTheme.collectAsState()
            val useDarkTheme = when (currentAppTheme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            SlavgorodBusTheme(darkTheme = useDarkTheme) {
                BusScheduleApp(themeViewModel = themeViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusScheduleApp(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val busViewModel: BusViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            busViewModel = busViewModel,
            themeViewModel = themeViewModel
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    busViewModel: BusViewModel,
    themeViewModel: ThemeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = busViewModel,
                modifier = Modifier
            )
        }

        composable(Screen.FavoriteTimes.route) {
            FavoriteTimesScreen(
                viewModel = busViewModel,
                modifier = Modifier
            )
        }

        composable(
            route = "schedule/{routeId}",
            arguments = listOf(
                navArgument("routeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId")
            val route = busViewModel.getRouteById(routeId)
            ScheduleScreen(
                route = route,
                onBackClick = { navController.popBackStack() },
                viewModel = busViewModel
            )
        }

        composable(
            route = "routeDetails/{routeId}",
            arguments = listOf(navArgument("routeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId")
            val route = busViewModel.getRouteById(routeId)
            RouteDetailsScreen(
                route = route,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                themeViewModel = themeViewModel,
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                modifier = Modifier
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                modifier = Modifier
            )
        }
    }
}