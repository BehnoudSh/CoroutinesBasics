package com.example.coroutinessample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coroutinessample.ui.theme.CoroutinesSampleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val RESULT_1 = "Result #1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var textValue by remember { mutableStateOf("") }

            CoroutinesSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content(textValue, modifier = Modifier.padding(innerPadding), {
                        //IO, Main, Default
                        CoroutineScope(IO).launch {
                            val result = fakeApiRequest()
                            withContext(Dispatchers.Main) {
                                textValue = result
                            }
                        }
                    })
                }
            }
        }
    }

    private suspend fun fakeApiRequest(): String {
        return getResult1FromApi()
    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1_000)
        return RESULT_1
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}

@Composable
fun Content(textTitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column {
        Button(onClick = onClick, modifier = modifier.padding(16.dp)) {
            Text(text = "Click me!")
        }
        Text(
            text = textTitle,
            modifier = modifier.padding(16.dp)
        )

    }

}
