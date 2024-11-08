package com.example.weather

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weather.screen.nav.SetupNavHost
import com.example.weather.ui.theme.WeatherTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var buttonPress = rememberSaveable() { mutableStateOf(false) }
            var ip = rememberSaveable() { mutableStateOf("") }
            WeatherTheme {
                if (buttonPress.value) {
                    SetupNavHost()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedTextField(value = ip.value, onValueChange = {
                            runBlocking {
                                com.example.weather.ip = it
                                ip.value = it
                            }
                        }, modifier = Modifier.fillMaxWidth())

                        Button(modifier = Modifier.fillMaxWidth(), onClick = {
                            buttonPress.value = true
                        }, enabled = ip.value.isNotEmpty()) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        }
    }
}

var ip = "" //жеский костыль
