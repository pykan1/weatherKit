package com.example.weather.screen.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun MainScreen() {
    val viewModel = MainViewModel()
    val state = viewModel.stateFlow.collectAsState().value
    LaunchedEffect(viewModel) {
        viewModel.loadData()
    }
    
    
    Text(text = state.s)

}