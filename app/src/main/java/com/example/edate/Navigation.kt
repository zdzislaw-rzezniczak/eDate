package com.example.edate

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Routes{
    Signup,
    SignIn,
    Home,
}


@Composable
fun Navigation(
    navController : NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SignIn.name
    ) {

        composable(route = Routes.SignIn.name) {
            LoginScreen(onNavToHomePage = {
                navController.navigate(Routes.Home.name) {
                    launchSingleTop = true
                    popUpTo(route = Routes.SignIn.name) {
                        inclusive = true
                    }
                }
            }, loginViewModel = loginViewModel) {

                navController.navigate(Routes.Home.name) {
                    launchSingleTop = true
                    popUpTo(Routes.SignIn.name) {
                        inclusive = true
                    }
                }

                navController.navigate(Routes.Signup.name) {
                    launchSingleTop = true
                    popUpTo(Routes.SignIn.name) {
                        inclusive = true
                    }
                }
            }
        }
        composable(route = Routes.Signup.name) {
            SignUpScreen(
                onNavToHomePage = {
                    navController.navigate(Routes.Home.name) {
                        popUpTo(Routes.Signup.name) { inclusive = true }
                    }
                }, loginViewModel = loginViewModel,

            ) {

                navController.navigate(Routes.SignIn.name)
            }

        }
        composable(route = Routes.Home.name) {
            Home()
        }
    }
}
