package com.example.coroutinessample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coroutinessample.ui.theme.CoroutinesSampleTheme
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


//Kotlin Coroutine Jobs

class ActivityStep2CoroutineJobs : ComponentActivity() {

    private val PROGRESS_START = 0f
    private val JOB_TIME = 4_000 //ms
    private lateinit var job: CompletableJob

    var textValueOut by mutableStateOf("")
    var buttonTitleValueOut by mutableStateOf("")
    var currentProgressValueOut by mutableFloatStateOf(PROGRESS_START)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var textValue by remember { mutableStateOf(textValueOut) }
            var buttonTitleValue by remember { mutableStateOf(buttonTitleValueOut) }
            var currentProgressValue by remember { mutableFloatStateOf(currentProgressValueOut) }

            LaunchedEffect(textValueOut, buttonTitleValueOut) {
                textValue = textValueOut
                buttonTitleValue = buttonTitleValueOut
            }

            CoroutinesSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content2(
                        textTitle = textValue,
                        modifier = Modifier.padding(innerPadding),
                        buttonTitle = buttonTitleValue,
                        currentProgress = currentProgressValue,
                        onClick = {
                            if (!::job.isInitialized)
                                initJob()

                            startJob()
                        })
                }
            }
        }
    }

    fun startJob() {
        buttonTitleValueOut = "Cancel Job"
        textValueOut = "Job Started"
        (0 until JOB_TIME).forEach { i ->
            Delay()
            currentProgressValueOut += (100f / JOB_TIME)
            buttonTitleValueOut = "Start Job$i"
            println("currentProgressValueOut: $currentProgressValueOut")
        }

    }

    fun initJob() {
        buttonTitleValueOut = "Start Job"
        textValueOut = ""
        job = Job()
        job.invokeOnCompletion {
            it?.message.let { it1 ->
                var msg = it1
                if (msg.isNullOrBlank()) {
                    msg = "Unknown Cancellation error"
                }
                println("$job was cancelled. Reason: $msg")
                showToast(msg)
            }
        }
        currentProgressValueOut = PROGRESS_START
    }

    // ❌ GlobalScope (Unstructured Concurrency)
// Concept: It is NOT bound to any component's Lifecycle. It lives as long as the app process is alive.
// Risk: It does NOT cancel when the UI (Activity/Fragment) is destroyed, leading to potential Memory Leaks and wasted resources.
// Use Case: Very rare. Only used for tasks that must continue executing even if the user completely leaves the screen.
    //fun useGlobalScopeExample() {
    //GlobalScope.launch {
    // ...
    //  }
    //}

    // ✅ CoroutineScope (Structured Concurrency)
// Concept: Bound to the Lifecycle of its owner (e.g., ViewModel, Activity, Fragment).
// Safety: It automatically CANCELS all running coroutines when the Lifecycle is destroyed (e.g., onCleared() in ViewModel).
// Use Case: The standard way for Network requests, Database operations, and UI updates.
// Note: In Android, prefer using built-in scopes like 'viewModelScope' or 'lifecycleScope'.
    // fun useStructuredScopeExample() {
    // Ideally, use: viewModelScope.launch { ... } or lifecycleScope.launch { ... }

    // Generic example:
    //  CoroutineScope(Dispatchers.IO).launch {
    // ...
    //}
    //}
    fun showToast(text: String) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@ActivityStep2CoroutineJobs, text, Toast.LENGTH_SHORT).show()

        }
    }


    @Composable
    fun Content2(
        textTitle: String,
        buttonTitle: String,
        currentProgress: Float,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Column(modifier = modifier) {
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
            Button(onClick = onClick, modifier = modifier.padding(16.dp)) {
                Text(text = buttonTitle)
            }
            Text(
                text = textTitle,
                modifier = modifier.padding(16.dp)
            )
        }
    }
}