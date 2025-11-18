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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coroutinessample.ui.theme.CoroutinesSampleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/*
  ***Network timeouts with Kotlin Coroutines***

 We use withTimeoutOrNull
*/
class MainActivity1 : ComponentActivity() {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"

    var textValueOut by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var textValue by remember { mutableStateOf(textValueOut) }

            LaunchedEffect(textValueOut) {
                textValue = textValueOut
            }

            CoroutinesSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content1(textValue, modifier = Modifier.padding(innerPadding), {
                        //IO, Main, Default
                        CoroutineScope(IO).launch {
                            fakeApiRequest()
                        }
                    })
                }
            }
        }
    }

    private suspend fun fakeApiRequest() {
        val result = withTimeoutOrNull(2000L) {
            val result1 = getResult1FromApi()
            println("debug: $result1")
            withContext(Main) {
                textValueOut = result1
            }
            val result2 = getResult2FromApi()
            println("debug: $result2")
            withContext(Main) {
                textValueOut = result2
            }
        }

        if (result == null) {
            withContext(Main) {
                textValueOut = "timed out"
            }
        }
    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(5_00)
        return RESULT_1
    }

    private suspend fun getResult2FromApi(): String {
        logThread("getResult2FromApi")
        delay(3_000)
        return RESULT_2
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}

@Composable
fun Content1(textTitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
