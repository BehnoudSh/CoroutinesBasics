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


/*
    ***Kotlin Coroutines Beginner***

Coroutines are not threads.
They are similar to threads, but they are not threads themselves.
You can think of them as a series of “jobs” that need to run in a certain environment — and that environment is a thread.

Multiple coroutine jobs can run on the same thread, even at the same time conceptually.

So when you write a suspend fun that needs to do some work, it must be executed inside a coroutine.

coroutineScope is basically a way to group coroutines (jobs).
It lets you organize multiple tasks together and control what happens to them — for example, when they finish, or if one of them gets cancelled, and so on.
It’s like saying: “run these jobs within the scope of a certain thread.”

Dispatchers:

IO: For network operations, API calls, reading from a database, etc.

Main: For work that must run on the main (UI) thread.

Default: For heavy computational tasks.

CoroutineScope(Dispatchers.IO).launch {
    // background work
}


After the work is done, you may want to update the UI somewhere.
Because the coroutine is running in the background, you cannot directly update the UI (it would cause a crash).
The solution is:

withContext(Dispatchers.Main) {
    // update UI here
}


The interesting part is that coroutines are somewhat independent of threads.
You can start a task in one coroutine, pass the result to another coroutine, and eventually bring the result back to the main thread to display it.
This is usuallt the pattern that we use most of the time
*/


class MainActivity : ComponentActivity() {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"
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
                            val result1 = getResult1FromApi()
                            withContext(Dispatchers.Main) {
                                textValue = result1
                            }
                            val result2 = getResult2FromApi()
                            withContext(Dispatchers.Main) {
                                textValue = result2
                            }
                        }
                    })
                }
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
