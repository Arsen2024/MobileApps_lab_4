package com.example.lab4_diary_app.Routes

sealed class Screen(val route: String) {
    object Onboarding: Screen("onboarding")
    object EnterName: Screen("enter_name")
    object Main: Screen("main/{userName}"){
        fun createRoute(userName: String) = "main/$userName"
    }
}