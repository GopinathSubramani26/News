package com.example.news

sealed class Screen(val route: String) {
    object NewsHomeScreen : Screen("NewsHomeScreen")
    object NewsArticleScreen: Screen("NewsArticleScreen")
}
