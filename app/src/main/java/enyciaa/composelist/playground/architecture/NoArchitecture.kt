package enyciaa.composelist.playground.architecture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.navigation.compose.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import enyciaa.composelist.playground.theming.ComposeListPlaygroundTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoArchitectureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListPlaygroundTheme {
                NoArchitectureApp()
            }
        }
    }
}

@Composable
fun NoArchitectureApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "question") {
        composable("question") {
            NoArchitectureQuestionDestination(
                onConfirm = {
                    val validatedAnswer = if (it.isBlank()) "wrong answer" else it
                    navController.navigate("result/$validatedAnswer")
                }
            )
        }
        composable(
            "result/{answer}",
            listOf(navArgument("answer") { type = NavType.StringType }),
        ) {
            NoArchitectureResultDestination(
                answer = it.arguments?.getString("answer") ?: ""
            )
        }
    }
}

@Composable
fun NoArchitectureQuestionDestination(
    onConfirm: (answer: String) -> Unit
) {
    val answerService = remember { AnswerService() }
    val isLoading = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "What do you call a mexican cheese?")
        val textState = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it }
        )
        if (isLoading.value) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                coroutineScope.launch {
                    isLoading.value = true
                    withContext(Dispatchers.IO) { answerService.save() }
                    isLoading.value = false
                    onConfirm(textState.value.text)
                }
            }) {
                Text(text = "Confirm")
            }
        }
    }
}

@Composable
fun NoArchitectureResultDestination(
    answer: String
) {
    val isCorrect = remember { answer == "Nacho cheese" }
    val textToDisplay = remember {
        if (isCorrect) {
            "You've heard too many cheese jokes"
        } else {
            "Nacho cheese"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = textToDisplay)
    }
}
